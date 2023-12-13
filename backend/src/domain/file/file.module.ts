import { Module } from '@nestjs/common';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { FileService } from '@/domain/file/file.service';
import { PrismaModule } from '@/common/prisma/prisma.module';
import { PrismaService } from '@/common/prisma/prisma.service';
import { FileRepository } from '@/domain/file/file.repository';

@Module({
  imports: [PrismaModule],
  providers: [PrismaService, PrismaProvider, FileService, FileRepository],
  exports: [FileService],
})
export class FileModule {}
