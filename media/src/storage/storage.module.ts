import { Module } from '@nestjs/common';
import { BucketService } from '@/storage/cloud/bucket.service';
import { StorageService } from '@/storage/storage.service';
import { ConfigService } from '@nestjs/config';

@Module({
  providers: [StorageService, BucketService, ConfigService],
  exports: [StorageService],
})
export class StorageModule {}
