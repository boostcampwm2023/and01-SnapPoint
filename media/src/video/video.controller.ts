import { Controller, Inject } from '@nestjs/common';
import { VideoService } from './video.service';
import {
  ClientProxy,
  Ctx,
  MessagePattern,
  Payload,
  RmqContext,
} from '@nestjs/microservices';

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
  @MessagePattern({ cmd: 'video.preprocess' })
  async preprocess(
    @Payload() dto: { uuid: string },
    @Ctx() context: RmqContext,
  ) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    const { shouldResize480P, shouldResize720P } =
      await this.videoService.preprocess(dto);

    channel.ack(originalMsg);
    // const { uuid } = dto;

    // 아닌 경우 트랜스코딩을 진행한다.
    // if (shouldResize480P) {
    //   this.client.emit('video.transcode', { uuid, quality: 480 });
    // }

    // if (shouldResize720P) {
    //   this.client.emit('video.transcode', { uuid, quality: 720 });
    // }
  }
}
