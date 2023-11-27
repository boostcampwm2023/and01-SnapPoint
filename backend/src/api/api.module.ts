import { Module } from '@nestjs/common';
import { FileApiController } from '@/api/file-api/file-api.controller';
import { FileApiService } from '@/api/file-api/file-api.service';
import { BucketService } from '@/common/bucket.service';
import { PrismaService } from '@/common/prisma/prisma.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { FileService } from '@/domain/file/file.service';

@Module({
  controllers: [FileApiController],
  providers: [BucketService, PrismaService, PrismaProvider, FileApiService, FileService],
})
export class ApiModule {}
