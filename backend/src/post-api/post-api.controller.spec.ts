import { Test, TestingModule } from '@nestjs/testing';
import { PostApiController } from './post-api.controller';
import { BlockService } from '@/block/block.service';
import { PrismaService } from '@/prisma.service';
import { PrismaModule } from '@/prisma/prisma.module';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { mockDeep } from 'jest-mock-extended';
import { BlockFileService } from '@/block-file/block-file.service';
import { BucketService } from '@/bucket.service';
import { FileService } from '@/file/file.service';
import { PostService } from '@/post/post.service';
import { PostApiService } from './post-api.service';

describe('PostApiController', () => {
  let controller: PostApiController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [PostApiController],
      imports: [PrismaModule],
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
      .overrideProvider(PrismaService)
      .useValue(mockDeep<PrismaService>())
      .compile();

    controller = module.get<PostApiController>(PostApiController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
