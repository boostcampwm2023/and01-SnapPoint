import { Test, TestingModule } from '@nestjs/testing';
import { PostApiController } from './post-api.controller';
import { PostApiService } from './post-api.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaService } from '@/common/prisma/prisma.service';
import { BlockService } from '@/domain/block/block.service';
import { FileService } from '@/domain/file/file.service';
import { PostService } from '@/domain/post/post.service';
import { ValidationService } from '../validation/validation.service';
import { TransformationService } from '../transformation/transformation.service';
import { RedisCacheService } from '@/common/redis/redis-cache.service';
import { mockDeep } from 'jest-mock-extended';
import { SummarizationService } from '../summarization/summarization.service';
import { BlockRepository } from '@/domain/block/block.repository';
import { HttpService } from '@nestjs/axios';
import { FileRepository } from '@/domain/file/file.repository';

describe('PostApiController', () => {
  let controller: PostApiController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [PostApiController],
      providers: [
        PostApiService,
        PrismaService,
        PrismaProvider,
        ValidationService,
        PostService,
        BlockService,
        FileService,
        TransformationService,
        RedisCacheService,
        SummarizationService,
        BlockRepository,
        HttpService,
        FileRepository,
      ],
    })
      .overrideProvider(RedisCacheService)
      .useValue(mockDeep<RedisCacheService>())
      .compile();

    controller = module.get<PostApiController>(PostApiController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
