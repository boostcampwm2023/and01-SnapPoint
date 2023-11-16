import { Module } from '@nestjs/common';
import { PostService } from './post.service';
import { PrismaService } from '@/prisma.service';
import { BlockService } from '@/block/block.service';

@Module({
  providers: [PostService, PrismaService, BlockService],
})
export class PostModule {}
