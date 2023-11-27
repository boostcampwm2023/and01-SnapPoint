import { Module } from '@nestjs/common';
import { PostApiService } from './post-api.service';
import { PostService } from '@/domain/post/post.service';
import { BlockService } from '@/domain/block/block.service';
import { BlockFileService } from '@/domain/block-file/block-file.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PostApiController } from './post-api.controller';
import { FileService } from '@/domain/file/file.service';
import { BucketService } from '@/common/bucket.service';
import { PrismaModule } from '@/common/prisma/prisma.module';
import { PrismaService } from '@/common/prisma/prisma.service';
import { UserService } from '@/domain/user/user.service';

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
    UserService,
  ],
})
export class PostApiModule {}
