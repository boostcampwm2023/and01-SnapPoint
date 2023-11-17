import { Module } from '@nestjs/common';
import { PostApiService } from './post-api.service';
import { PostService } from '@/post/post.service';
import { BlockService } from '@/block/block.service';
import { BlockFileService } from '@/block-file/block-file.service';
import { PrismaService } from '@/prisma.service';
import { PostApiController } from './post-api.controller';

@Module({
  controllers: [PostApiController],
  providers: [PostApiService, PostService, BlockService, BlockFileService, PrismaService],
})
export class PostApiModule {}
