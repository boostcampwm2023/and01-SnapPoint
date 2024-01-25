import { Test, TestingModule } from '@nestjs/testing';
import { PostApiController } from './post-api.controller';
import { PostApiService } from './post-api.service';
import { BlockService } from '@/domain/block/block.service';
import { FileService } from '@/domain/file/file.service';
import { PostService } from '@/domain/post/post.service';
import { ValidationService } from '../validation/validation.service';
import { TransformationService } from '../transformation/transformation.service';
import { RedisCacheService } from '@/common/redis/redis-cache.service';
import { mockDeep } from 'jest-mock-extended';
import { BlockRepository } from '@/domain/block/block.repository';
import { HttpService } from '@nestjs/axios';
import { FileRepository } from '@/domain/file/file.repository';
import { RedisManager } from '@liaoliaots/nestjs-redis';
import { ConfigService } from '@nestjs/config';
import { UserService } from '@/domain/user/user.service';
import { PRISMA_SERVICE, PrismaService } from '@/common/databases/prisma.service';
import { ClientProxy } from '@nestjs/microservices';
import { UtilityModule } from '@/common/utility/utility.module';

describe('PostApiController', () => {
  let controller: PostApiController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [PostApiController],
      imports: [UtilityModule],
      providers: [
        PostApiService,
        ValidationService,
        PostService,
        BlockService,
        FileService,
        TransformationService,
        RedisCacheService,
        RedisManager,
        BlockRepository,
        HttpService,
        FileRepository,
        ConfigService,
        UserService,
        {
          provide: PRISMA_SERVICE,
          useValue: mockDeep<PrismaService>(),
        },
        {
          provide: 'SUMMARY_SERVICE',
          useValue: mockDeep<ClientProxy>(),
        },
      ],
    })
      .overrideProvider(RedisManager)
      .useValue(mockDeep<RedisManager>())
      .overrideProvider(HttpService)
      .useValue(mockDeep<HttpService>())
      .compile();

    controller = module.get<PostApiController>(PostApiController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
