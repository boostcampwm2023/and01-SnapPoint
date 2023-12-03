import { FileService } from '@/domain/file/file.service';
import { ConflictException, ForbiddenException, Injectable, NotFoundException } from '@nestjs/common';
import { FileDto } from './dto/file.dto';
import { File } from '@prisma/client';
import { AttachFileDto } from './dto/attach-file.dto';
import { CreateFileDataDto } from './dto/create-file-data.dto';

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

  async createFile(createFileDataDto: CreateFileDataDto): Promise<FileDto> {
    const createdFile = await this.fileService.createFile(createFileDataDto);
    return FileDto.of(createdFile);
  }

  async findFile(uuid: string, userUuid: string): Promise<FileDto> {
    const file = await this.accessFile(uuid, userUuid);
    return FileDto.of(file);
  }

  async findFiles(userUuid: string): Promise<FileDto[]> {
    const files = await this.fileService.findFiles({ where: { userUuid } });

    return files.map((file) => FileDto.of(file));
  }

  async attachFile(uuid: string, userUuid: string, attachFileDto: AttachFileDto): Promise<FileDto> {
    const file = await this.accessFile(uuid, userUuid);

    if (file.sourceUuid) {
      throw new ConflictException('The file has been already attached with other resource.');
    }

    return this.fileService.updateFile({ where: { uuid }, data: attachFileDto });
  }

  async deleteFile(uuid: string, userUuid: string): Promise<FileDto> {
    const file = await this.accessFile(uuid, userUuid);
    return this.fileService.deleteFile({ uuid: file.uuid });
  }
}
