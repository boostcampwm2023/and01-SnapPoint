import { Injectable } from '@nestjs/common';
import { FileRepository } from '@/domain/file/file.repository';
import { FindFilesBySourceDto } from '@/domain/file/dtos/find-files-by-source.dto';
import { UpdateFileDto } from './dtos/update-file.dto';
import { ProcessFileDto } from '@/domain/file/dtos/process-file.dto';
import { CreateFileDto } from '@/domain/file/dtos/create-file.dto';
import { FindFilesByIdDto } from './dtos/find-files-by-id.dto';
import { File } from '@prisma/client';

@Injectable()
export class FileService {
  constructor(private readonly repository: FileRepository) {}

  async createFile(dto: CreateFileDto) {
    return this.repository.createFile(dto);
  }

  async processFile(dto: ProcessFileDto) {
    const { uuid } = dto;
    return this.repository.updateFile({ where: { uuid }, data: { isProcessed: true } });
  }

  async findFilesById(dtos: FindFilesByIdDto[]) {
    return this.repository.findFiles({ where: { OR: dtos } });
  }

  async findFilesBySources(source: string, dtos: FindFilesBySourceDto[]) {
    const conditions = dtos.map((dto) => ({ sourceUuid: dto.uuid }));
    return this.repository.findFiles({ where: { OR: conditions, AND: { source } } });
  }

  async attachFiles(dtos: UpdateFileDto[]) {
    return Promise.all(
      dtos.map((file) => {
        const { uuid, source, sourceUuid, thumbnailUuid } = file;
        return this.repository.updateFile({ where: { uuid }, data: { source, sourceUuid, thumbnailUuid } });
      }),
    );
  }

  async modifyFiles(dtos: UpdateFileDto[]) {
    await this.repository.deleteFiles({ OR: dtos });
    return Promise.all(
      dtos.map((dto) => this.repository.updateFile({ where: { uuid: dto.uuid }, data: { ...dto, isDeleted: false } })),
    );
  }

  filterNotProcessedFiles(files: File[]) {
    return files.filter((file) => file.isProcessed === false);
  }
}
