import { Controller, Inject, Logger } from '@nestjs/common';
import { VideoService } from './video.service';
import {
  ClientProxy,
  Ctx,
  MessagePattern,
  Payload,
  RmqContext,
} from '@nestjs/microservices';
import { TargetdataDto } from './dtos/targetdata.dto';
import { ProcessVideoDto } from './dtos/process-video.dto';

@Controller('video')
export class VideoController {
  constructor(
    private readonly videoService: VideoService,
    @Inject('RMQ_SERVICE') private readonly client: ClientProxy,
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

    const { targets, processData } = await this.videoService.parseMetadata(dto);

    channel.ack(originalMsg);

    targets.forEach((targetData) => {
      this.client.emit({ cmd: 'video.transcode' }, { targetData, processData });
    });
  }

  @MessagePattern({ cmd: 'video.transcode' })
  async transcode(
    @Payload()
    payload: { targetData: TargetdataDto; processData: ProcessVideoDto },
    @Ctx() context: RmqContext,
  ) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    const { targetData, processData } = payload;

    Logger.debug(`transcode: ${processData.uuid} to ${targetData.quality}`);

    this.videoService.transcode(targetData, processData);

    channel.ack(originalMsg);
  }
}
