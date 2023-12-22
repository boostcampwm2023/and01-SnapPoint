import { Module } from '@nestjs/common';
import { PostService } from './post.service';

@Module({
  providers: [PostService],
})
export class PostModule {}
