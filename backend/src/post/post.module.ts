import { Module } from '@nestjs/common';
import { PostService } from './post.service';
import { PrismaProvider } from '@/prisma.service';
import { BlockService } from '@/block/block.service';

@Module({
  providers: [PostService, PrismaProvider, BlockService],
})
export class PostModule {}
