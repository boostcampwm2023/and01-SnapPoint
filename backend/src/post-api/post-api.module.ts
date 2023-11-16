import { Module } from '@nestjs/common';
import { PostApiService } from './post-api.service';
import { PostService } from '@/post/post.service';
import { BlockService } from '@/block/block.service';
import { BlockFileService } from '@/block-file/block-file.service';

@Module({
  providers: [PostApiService, PostService, BlockService, BlockFileService],
})
export class PostApiModule {}
