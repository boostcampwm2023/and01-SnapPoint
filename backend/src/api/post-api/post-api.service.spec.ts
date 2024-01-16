import { ClientProxy } from '@nestjs/microservices';
import { ModifyPostDto } from './dtos/post/modify-post.dto';
import { Test, TestingModule } from '@nestjs/testing';
import { PostApiService } from './post-api.service';
import { ValidationService } from '../validation/validation.service';
import { PostService } from '@/domain/post/post.service';
import { BlockService } from '@/domain/block/block.service';
import { FileService } from '@/domain/file/file.service';
import { DeepMockProxy, mockDeep } from 'jest-mock-extended';
import { ForbiddenException, NotFoundException } from '@nestjs/common';
import { Post } from '@prisma/client';
import { TransformationService } from '../transformation/transformation.service';
import { RedisCacheService } from '@/common/redis/redis-cache.service';
import { HttpService } from '@nestjs/axios';
import { ConfigService } from '@nestjs/config';
import { UserService } from '@/domain/user/user.service';
import { PRISMA_SERVICE, PrismaService } from '@/common/databases/prisma.service';

describe('PostApiService', () => {
  let service: PostApiService;
  let postService: DeepMockProxy<PostService>;
  let postDto: ModifyPostDto;
  let postEntity: Post;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        PostApiService,
        ValidationService,
        TransformationService,
        PostService,
        BlockService,
        FileService,
        RedisCacheService,
        HttpService,
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
      .overrideProvider(PostService)
      .useValue(mockDeep<PostService>())
      .overrideProvider(BlockService)
      .useValue(mockDeep<BlockService>())
      .overrideProvider(FileService)
      .useValue(mockDeep<FileService>())
      .overrideProvider(RedisCacheService)
      .useValue(mockDeep<RedisCacheService>())
      .overrideProvider(HttpService)
      .useValue(mockDeep<HttpService>())
      .overrideProvider(UserService)
      .useValue(mockDeep<UserService>())
      .compile();

    service = module.get<PostApiService>(PostApiService);
    postService = module.get(PostService);

    postDto = {
      title: 'Test Post',
      blocks: [
        {
          uuid: 'mock-block-uuid-1',
          type: 'text',
          content: 'this is text block',
        },
      ],
    };

    postEntity = {
      id: 1,
      uuid: 'mock-post-uuid',
      userUuid: 'mock-user-uuid',
      title: 'Test Post',
      summary: '',
      createdAt: new Date('2023-11-23T15:02:10.626Z'),
      modifiedAt: new Date('2023-11-23T15:02:10.626Z'),
      isDeleted: false,
    };
  });

  describe('readPost()', () => {
    it('게시글을 찾지 못한 경우 NotFoundExcpetion을 발생한다.', () => {
      postService.findPost.mockResolvedValue(null);
      expect(service.modifyPost('not-exist-uuid', 'mock-user-uuid', postDto)).rejects.toThrow(NotFoundException);
    });
  });

  describe('modifyPost()', () => {
    it('게시글을 찾지 못한 경우 NotFoundExcpetion을 발생한다.', () => {
      postService.findPost.mockResolvedValue(null);
      expect(service.modifyPost('not-exist-uuid', 'mock-user-uuid', postDto)).rejects.toThrow(NotFoundException);
    });

    it('자신의 게시글이 아닐 경우 ForbiddenExcpetion을 발생한다.', () => {
      postService.findPost.mockResolvedValue(postEntity);
      expect(service.modifyPost('mock-post-uuid', 'not-exist-uuid', postDto)).rejects.toThrow(ForbiddenException);
    });
  });
});
