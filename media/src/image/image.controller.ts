import { ImageService } from './image.service';
import { Controller, Inject, Logger } from '@nestjs/common';
import { ClientProxy, MessagePattern, Payload } from '@nestjs/microservices';
import { ResizeImageDto } from './dtos/resize-image.dto';

@Controller()
export class ImageController {
  constructor(
    private readonly imageService: ImageService,
    @Inject('MAIN_SERVICE') private readonly client: ClientProxy,
  ) {}

  @MessagePattern({ cmd: 'process_image' })
  async processImage(@Payload() resizeImageDto: ResizeImageDto) {
    Logger.debug(`Process ${resizeImageDto.uuid}`);
    await this.imageService.resizeImage(resizeImageDto);
    return this.client.emit(
      { res: 'process_image' },
      { uuid: resizeImageDto.uuid },
    );
  }
}
