import { Module } from '@nestjs/common';
import { PostService } from './post.service';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { BlockService } from '@/block/block.service';
import { PrismaModule } from '@/prisma/prisma.module';
import { PrismaService } from '@/prisma.service';

@Module({
  imports: [PrismaModule],
  providers: [PostService, PrismaService, PrismaProvider, BlockService],
})
export class PostModule {}
