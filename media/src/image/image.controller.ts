import { ImageService } from './image.service';
import { Controller, Inject, Logger } from '@nestjs/common';
import { ClientProxy, MessagePattern, Payload } from '@nestjs/microservices';
import { ResizeImageDto } from './dtos/resize-image.dto';

@Controller()
export class ImageController {
  constructor(
    private readonly imageService: ImageService,
    @Inject('DATA_SERVICE') private readonly client: ClientProxy,
  ) {}

  @MessagePattern({ cmd: 'process_image' })
  async processImage(@Payload() resizeImageDto: ResizeImageDto) {
    Logger.debug(`Process ${resizeImageDto.uuid}`);
    await this.imageService.resizeImage(resizeImageDto);
    return this.client.emit(
      { cmd: 'process_image_data' },
      { uuid: resizeImageDto.uuid },
    );
  }
}
