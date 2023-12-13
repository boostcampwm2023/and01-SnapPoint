import { Controller, Inject, Logger } from '@nestjs/common';

import { FileApiService } from './file-api.service';
import { ClientProxy, Ctx, MessagePattern, Payload, RmqContext } from '@nestjs/microservices';
import { CreateFileDataDto } from './dto/create-file-data.dto';
import { ApplyProcessFileDto } from './dto/apply-process-file.dto';

@Controller('files')
export class FileApiController {
  constructor(
    private readonly fileApiService: FileApiService,
    @Inject('MEDIA_SERVICE') private readonly client: ClientProxy,
  ) {}

  @MessagePattern({ cmd: 'create_image_data' })
  async createImageData(@Payload() createFileDataDto: CreateFileDataDto, @Ctx() context: RmqContext) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    await this.fileApiService.createFile(createFileDataDto);
    channel.ack(originalMsg);

    return this.client.emit({ cmd: 'process_image' }, { uuid: createFileDataDto.uuid });
  }

  @MessagePattern({ cmd: 'video.create' })
  async createVideoData(@Payload() createFileDataDto: CreateFileDataDto, @Ctx() context: RmqContext) {
    Logger.debug(`create ${createFileDataDto.uuid}`);
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    await this.fileApiService.createFile(createFileDataDto);
    channel.ack(originalMsg);

    return this.client.emit({ cmd: 'video.process' }, { uuid: createFileDataDto.uuid });
  }

  @MessagePattern({ cmd: 'media.processed' })
  async procssImageData(@Payload() applyDataDto: ApplyProcessFileDto, @Ctx() context: RmqContext) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    Logger.debug(`Apply ${applyDataDto.uuid}`);

    await this.fileApiService.applyFile(applyDataDto);

    channel.ack(originalMsg);
  }
}
