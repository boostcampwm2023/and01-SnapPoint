import { Controller, Inject, Logger } from '@nestjs/common';
import { VideoService } from './video.service';
import {
  ClientProxy,
  Ctx,
  MessagePattern,
  Payload,
  RmqContext,
} from '@nestjs/microservices';
import { ProcessVideoDto } from './dtos/process-video.dto';
import { VideoManagerService } from './video-manager.service';

@Controller('video')
export class VideoController {
  constructor(
    private readonly videoService: VideoService,
    private readonly videoManager: VideoManagerService,
    @Inject('RMQ_SERVICE') private readonly client: ClientProxy,
    @Inject('DATA_SERVICE') private readonly dataClient: ClientProxy,
  ) {}

  /**
   * 비디오의 메타 데이터를 불러오고, FPS와 인코딩을 처리한다.
   * @param dto
   * @returns
   */
  @MessagePattern({ cmd: 'video.process' })
  async process(@Payload() dto: { uuid: string }, @Ctx() context: RmqContext) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    channel.ack(originalMsg);
    const { targets, processData } = await this.videoService.parseMetadata(dto);

    this.videoManager.emitProcessVideo(dto.uuid, targets, processData);
  }

  @MessagePattern({ cmd: 'video.transcode' })
  async transcode(
    @Payload()
    payload: { quality: number; processData: ProcessVideoDto },
    @Ctx() context: RmqContext,
  ) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    const { quality, processData } = payload;

    const { uuid } = processData;

    Logger.debug(`transcode: ${uuid} to ${quality}`);
    channel.ack(originalMsg);
    await this.videoService.transcode(quality, processData);

    this.client.emit({ cmd: 'video.package' }, { uuid, quality });
  }

  @MessagePattern({ cmd: 'video.package' })
  async package(
    @Payload() dto: { uuid: string; quality: number },
    @Ctx() context: RmqContext,
  ) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    Logger.debug(`package: ${dto.uuid}`);
    channel.ack(originalMsg);
    await this.videoService.package(dto);

    this.client.emit({ cmd: 'video.packaged' }, dto);
  }
  @MessagePattern({ cmd: 'video.packaged' })
  async handleVideoProcess(
    @Payload() dto: { uuid: string; quality: number },
    @Ctx() context: RmqContext,
  ) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    this.videoManager.handleVideoProcess(dto);
    channel.ack(originalMsg);
  }

  @MessagePattern({ cmd: 'video.end' })
  async end(
    @Payload() dto: { uuid: string; qualities: number[] },
    @Ctx() context: RmqContext,
  ) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();
    Logger.debug(`package: ${dto.uuid}`);

    channel.ack(originalMsg);
    await this.videoService.end(dto);
    this.dataClient.emit({ cmd: 'media.processed' }, { uuid: dto.uuid });
  }
}
