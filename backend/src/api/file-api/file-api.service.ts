import { FileService } from '@/domain/file/file.service';
import { ForbiddenException, Injectable, NotFoundException } from '@nestjs/common';
import { FileDto } from './dto/file.dto';
import { File } from '@prisma/client';
import { CreateFileDataDto } from './dto/create-file-data.dto';
import { ApplyProcessFileDto } from './dto/apply-process-file.dto';

@Injectable()
export class FileApiService {
  constructor(private readonly fileService: FileService) {}

  private async accessFile(uuid: string, userUuid: string): Promise<File> {
    const file = await this.fileService.findFile({ uuid });

    if (!file || file.isDeleted) {
      throw new NotFoundException(`Could not found the file with uuid: ${uuid}`);
    }

    if (file.userUuid !== userUuid) {
      throw new ForbiddenException('Could not access this file. please check your permission.');
    }

    return file;
  }

  async createFile(createFileDataDto: CreateFileDataDto, isProcessed: boolean = false): Promise<FileDto> {
    const createdFile = await this.fileService.createFile({ ...createFileDataDto, isProcessed });
    return FileDto.of(createdFile);
  }

  async applyFile(applyFileDto: ApplyProcessFileDto) {
    const { uuid } = applyFileDto;
    await this.fileService.updateFile({ where: { uuid }, data: { isProcessed: true } });
  }

  async findFile(uuid: string, userUuid: string): Promise<FileDto> {
    const file = await this.accessFile(uuid, userUuid);
    return FileDto.of(file);
  }

  async findFiles(userUuid: string): Promise<FileDto[]> {
    const files = await this.fileService.findFiles({ where: { userUuid } });

    return files.map((file) => FileDto.of(file));
  }
}
