import { Module } from '@nestjs/common';
import { FileController } from './file.controller';
import { BucketService } from '@/bucket.service';
import { PrismaProvider } from '@/prisma.service';
import { FileService } from './file.service';

@Module({
  controllers: [FileController],
  providers: [BucketService, PrismaProvider, FileService],
})
export class FileModule {}
