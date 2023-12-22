import { Module } from '@nestjs/common';
import { FileService } from '@/domain/file/file.service';
import { FileRepository } from '@/domain/file/file.repository';

@Module({
  providers: [FileService, FileRepository],
  exports: [FileService],
})
export class FileModule {}
