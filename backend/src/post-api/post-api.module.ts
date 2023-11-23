import { Module } from '@nestjs/common';
import { PostApiService } from './post-api.service';
import { PostService } from '@/post/post.service';
import { BlockService } from '@/block/block.service';
import { BlockFileService } from '@/block-file/block-file.service';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { PostApiController } from './post-api.controller';
import { FileService } from '@/file/file.service';
import { BucketService } from '@/bucket.service';
import { PrismaModule } from '@/prisma/prisma.module';
import { PrismaService } from '@/prisma.service';

@Module({
  imports: [PrismaModule],
  controllers: [PostApiController],
  providers: [
    PostApiService,
    PrismaService,
    PostService,
    BucketService,
    BlockService,
    BlockFileService,
    PrismaProvider,
    FileService,
  ],
})
export class PostApiModule {}
