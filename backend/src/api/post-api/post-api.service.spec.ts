import { ClientProxy } from '@nestjs/microservices';
import { Test, TestingModule } from '@nestjs/testing';
import { PostApiService } from './post-api.service';
import { ValidationService } from '../validation/validation.service';
import { PostService } from '@/domain/post/post.service';
import { BlockService } from '@/domain/block/block.service';
import { FileService } from '@/domain/file/file.service';
import { DeepMockProxy, mockDeep } from 'jest-mock-extended';
import { NotFoundException } from '@nestjs/common';
import { TransformationService } from '../transformation/transformation.service';
import { RedisCacheService } from '@/common/redis/redis-cache.service';
import { UserService } from '@/domain/user/user.service';
import { mockPost, mockUser } from './mocks/mock.entity.post-api';
import { mockPostSimpleDto } from './mocks/mock.dto.post-api';

describe('PostApiService', () => {
  let service: PostApiService;

  let postService: DeepMockProxy<PostService>;
  let userService: DeepMockProxy<UserService>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        PostApiService,
        PostService,
        BlockService,
        FileService,
        UserService,
        ValidationService,
        TransformationService,
        RedisCacheService,
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
      .overrideProvider(UserService)
      .useValue(mockDeep<UserService>())
      .overrideProvider(RedisCacheService)
      .useValue(mockDeep<RedisCacheService>())
      .overrideProvider(ValidationService)
      .useValue(mockDeep<ValidationService>())
      .overrideProvider(TransformationService)
      .useValue(mockDeep<TransformationService>())
      .compile();

    service = module.get<PostApiService>(PostApiService);
    postService = module.get(PostService);
    userService = module.get(UserService);
  });

  describe('findPost()', () => {
    it('게시글을 간편 조회한 경우, 하위 블록 및 파일 정보를 반환하지 않는다.', () => {
      postService.findPost.mockResolvedValue(mockPost());
      userService.findUserById.mockResolvedValue(mockUser());

      expect(service.findPost('mock-post-uuid', false)).resolves.toEqual(mockPostSimpleDto());
    });

    it('게시글을 찾지 못한 경우 NotFoundExcpetion을 발생한다.', () => {
      postService.findPost.mockResolvedValue(null);
      expect(service.findPost('not-exist-uuid')).rejects.toThrow(NotFoundException);
    });

    it('게시글을 찾았지만, 사용자 정보가 없는 경우 NotFoundExecption을 발생한다.', () => {
      postService.findPost.mockResolvedValue(mockPost());
      userService.findUserById.mockResolvedValue(null);

      expect(service.findPost('mock-post-uuid')).rejects.toThrow(NotFoundException);
    });
  });
});
