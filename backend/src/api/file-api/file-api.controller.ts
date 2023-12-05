import { Controller, Inject, Logger } from '@nestjs/common';

import { FileApiService } from './file-api.service';
import { ClientProxy, MessagePattern, Payload } from '@nestjs/microservices';
import { CreateFileDataDto } from './dto/create-file-data.dto';
import { ApplyProcessFileDto } from './dto/apply-process-file.dto';

@Controller('files')
export class FileApiController {
  constructor(
    private readonly fileApiService: FileApiService,
    @Inject('MEDIA_SERVICE') private readonly client: ClientProxy,
  ) {}

  @MessagePattern({ cmd: 'create_image_data' })
  async createFileData(@Payload() createFileDataDto: CreateFileDataDto) {
    this.client.emit({ cmd: 'process_image' }, { uuid: createFileDataDto.uuid });
    return this.fileApiService.createFile(createFileDataDto);
  }

  @MessagePattern({ cmd: 'process_image_data' })
  async procssImageData(@Payload() applyDataDto: ApplyProcessFileDto) {
    Logger.debug(`Apply ${applyDataDto.uuid}`);
    return this.fileApiService.applyFile(applyDataDto);
  }

  async deleteFileData() {}

  async accessFile() {}
}
