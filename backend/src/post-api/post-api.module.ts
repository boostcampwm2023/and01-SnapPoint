import { Module } from '@nestjs/common';
import { PostApiService } from './post-api.service';
import { PostService } from '@/post/post.service';
import { BlockService } from '@/block/block.service';
import { BlockFileService } from '@/block-file/block-file.service';
import { PrismaProvider } from '@/prisma.service';
import { PostApiController } from './post-api.controller';
import { FileService } from '@/file/file.service';
import { BucketService } from '@/bucket.service';

@Module({
  controllers: [PostApiController],
  providers: [PostApiService, PostService, BucketService, BlockService, BlockFileService, PrismaProvider, FileService],
})
export class PostApiModule {}
