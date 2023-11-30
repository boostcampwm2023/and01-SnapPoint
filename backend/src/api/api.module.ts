import { Module } from '@nestjs/common';
import { FileApiController } from '@/api/file-api/file-api.controller';
import { FileApiService } from '@/api/file-api/file-api.service';
import { PostApiController } from '@/api/post-api/post-api.controller';
import { BucketService } from '@/common/bucket.service';
import { PrismaService } from '@/common/prisma/prisma.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { FileService } from '@/domain/file/file.service';
import { PostApiService } from '@/api/post-api/post-api.service';
import { ValidationService } from './validation/validation.service';
import { BlockService } from '@/domain/block/block.service';
import { PostService } from '@/domain/post/post.service';
import { SnapPointController } from './snap-point/snap-point.controller';
import { SnapPointService } from './snap-point/snap-point.service';
import { TransformationService } from './transformation/transformation.service';

@Module({
  controllers: [FileApiController, PostApiController, SnapPointController],
  providers: [
    BucketService,
    PrismaService,
    PrismaProvider,
    FileApiService,
    PostApiService,
    ValidationService,
    FileService,
    BlockService,
    PostService,
    SnapPointService,
    TransformationService,
  ],
})
export class ApiModule {}
