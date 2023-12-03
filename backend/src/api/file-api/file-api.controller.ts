import { Controller, Inject } from '@nestjs/common';

import { FileApiService } from './file-api.service';
import { ClientProxy, MessagePattern, Payload } from '@nestjs/microservices';
import { CreateFileDataDto } from './dto/create-file-data.dto';

@Controller('files')
export class FileApiController {
  constructor(private readonly fileApiService: FileApiService) {}

  @MessagePattern({ cmd: 'upload_file' })
  async createFileData(@Payload() createFileDataDto: CreateFileDataDto) {
    console.log(createFileDataDto);
    return this.fileApiService.createFile(createFileDataDto);
  }

  async deleteFileData() {}

  async accessFile() {}
}
