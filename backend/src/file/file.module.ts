import { Module } from '@nestjs/common';
import { FileController } from './file.controller';
import { BucketService } from '@/bucket.service';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { FileService } from './file.service';
import { PrismaModule } from '@/prisma/prisma.module';
import { PrismaService } from '@/prisma.service';

@Module({
  imports: [PrismaModule],
  controllers: [FileController],
  providers: [BucketService, PrismaService, PrismaProvider, FileService],
})
export class FileModule {}
