import { Test, TestingModule } from '@nestjs/testing';
import { PostApiService } from './post-api.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { ValidationService } from '../validation/validation.service';
import { PostService } from '@/domain/post/post.service';
import { BlockService } from '@/domain/block/block.service';
import { FileService } from '@/domain/file/file.service';
import { PrismaService } from '@/common/prisma/prisma.service';
import { DeepMockProxy, mockDeep } from 'jest-mock-extended';
import { mockPrismaProvider } from '@/common/mocks/mock.prisma';
import { ForbiddenException, NotFoundException } from '@nestjs/common';
import { Post } from '@prisma/client';
import { TransformationService } from '../transformation/transformation.service';
import { WritePostDto } from './dtos/write-post.dto';
import { RedisCacheService } from '@/common/redis/redis-cache.service';
import { SummarizationService } from '../summarization/summarization.service';

describe('PostApiService', () => {
  let service: PostApiService;
  let postService: DeepMockProxy<PostService>;
  let postDto: WritePostDto;
  let postEntity: Post;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        PostApiService,
        PrismaService,
        PrismaProvider,
        ValidationService,
        TransformationService,
        PostService,
        BlockService,
        FileService,
        RedisCacheService,
        SummarizationService,
      ],
    })
      .overrideProvider(PrismaProvider)
      .useValue(mockPrismaProvider)
      .overrideProvider(PostService)
      .useValue(mockDeep<PostService>())
      .overrideProvider(BlockService)
      .useValue(mockDeep<BlockService>())
      .overrideProvider(FileService)
      .useValue(mockDeep<FileService>())
      .overrideProvider(RedisCacheService)
      .useValue(mockDeep<RedisCacheService>())
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
