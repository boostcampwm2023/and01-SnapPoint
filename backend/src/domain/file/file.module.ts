import { Module } from '@nestjs/common';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { FileService } from './file.service';
import { PrismaModule } from '@/common/prisma/prisma.module';
import { PrismaService } from '@/common/prisma/prisma.service';

@Module({
  imports: [PrismaModule],
  providers: [PrismaService, PrismaProvider, FileService],
})
export class FileModule {}
