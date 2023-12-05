import { Module } from '@nestjs/common';
import { BucketService } from '@/upload/storages/bucket.service';
import { UploadService } from '@/upload/upload.service';
import { ConfigService } from '@nestjs/config';

@Module({
  providers: [UploadService, BucketService, ConfigService],
  exports: [UploadService],
})
export class UploadModule {}
