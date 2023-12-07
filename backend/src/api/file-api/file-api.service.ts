import { RedisCacheService } from '@/common/redis/redis-cache.service';
import { FileService } from '@/domain/file/file.service';
import { Injectable } from '@nestjs/common';
import { FileDto } from './dto/file.dto';
import { CreateFileDataDto } from './dto/create-file-data.dto';
import { ApplyProcessFileDto } from './dto/apply-process-file.dto';

@Injectable()
export class FileApiService {
  constructor(
    private readonly fileService: FileService,
    private readonly redisService: RedisCacheService,
  ) {}

  async createFile(createFileDataDto: CreateFileDataDto, isProcessed: boolean = false): Promise<FileDto> {
    const createdFile = await this.fileService.createFile({ ...createFileDataDto, isProcessed });
    return FileDto.of(createdFile);
  }

  async applyFile(applyFileDto: ApplyProcessFileDto) {
    const updatedFile = await this.fileService.processFile(applyFileDto);
    this.redisService.del(`file:${updatedFile.uuid}`);
    return FileDto.of(updatedFile);
  }
}
