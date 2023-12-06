import { ImageService } from './image.service';
import { Controller, Inject, Logger } from '@nestjs/common';
import {
  ClientProxy,
  Ctx,
  MessagePattern,
  Payload,
  RmqContext,
} from '@nestjs/microservices';
import { ResizeImageDto } from './dtos/resize-image.dto';

@Controller()
export class ImageController {
  constructor(
    private readonly imageService: ImageService,
    @Inject('DATA_SERVICE') private readonly client: ClientProxy,
  ) {}

  @MessagePattern({ cmd: 'process_image' })
  async processImage(
    @Payload() resizeImageDto: ResizeImageDto,
    @Ctx() context: RmqContext,
  ) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    Logger.debug(`Process ${resizeImageDto.uuid}`);
    await this.imageService.resizeImage(resizeImageDto);

    channel.ack(originalMsg);

    this.client.emit(
      { cmd: 'process_image_data' },
      { uuid: resizeImageDto.uuid },
    );
  }
}
