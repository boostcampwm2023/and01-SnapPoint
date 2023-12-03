import { Module } from '@nestjs/common';
import { FileApiController } from '@/api/file-api/file-api.controller';
import { FileApiService } from '@/api/file-api/file-api.service';
import { PostApiController } from '@/api/post-api/post-api.controller';
import { PrismaService } from '@/common/prisma/prisma.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { FileService } from '@/domain/file/file.service';
import { PostApiService } from '@/api/post-api/post-api.service';
import { ValidationService } from './validation/validation.service';
import { BlockService } from '@/domain/block/block.service';
import { PostService } from '@/domain/post/post.service';
import { TransformationService } from './transformation/transformation.service';
import { ClientsModule, Transport } from '@nestjs/microservices';

@Module({
  controllers: [FileApiController, PostApiController],
  providers: [
    PrismaService,
    PrismaProvider,
    FileApiService,
    PostApiService,
    ValidationService,
    FileService,
    BlockService,
    PostService,
    TransformationService,
  ],
})
export class ApiModule {}
