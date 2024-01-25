import { Test, TestingModule } from '@nestjs/testing';
import { TransformationService } from './transformation.service';
import {
  mockDecomposedImagePostDto,
  mockDecomposedVideoPostDto,
  mockImagePostDto,
  mockPostDto,
  mockVideoPostDto,
} from './mocks/mock.dto.transform';
import { mockBlocks, mockFiles, mockPost, mockUserPayload } from './mocks/mock.entity.transform';
import { UtilityService } from '@/common/utility/utility.service';

describe('TransformationService', () => {
  let service: TransformationService;

  const postUuid = 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e';

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [TransformationService, UtilityService],
    }).compile();

    service = module.get<TransformationService>(TransformationService);
  });

  describe('decomposePostData()', () => {
    it('계층적인 게시글, 블록, 파일 데이터를 각각 분할할 수 있다.', () => {
      const postDto = mockImagePostDto();
      const decomposedPostData = mockDecomposedImagePostDto();

      expect(service.decomposePostData(postDto, postUuid)).toEqual(decomposedPostData);
    });

    it('썸네일이 있는 동영상 데이터를 두 개의 파일로 분할할 수 있다.', () => {
      const postDto = mockVideoPostDto();
      const decomposedPostData = mockDecomposedVideoPostDto();

      expect(service.decomposePostData(postDto, postUuid)).toEqual(decomposedPostData);
    });
  });

  describe('assemblePost()', () => {
    it('게시글, 블록, 파일, 사용자 정보를 받아 계층적 구조로 조립할 수 있다.', () => {
      const post = mockPost();
      const userPayload = mockUserPayload();
      const blocks = mockBlocks();
      const files = mockFiles();

      expect(service.assemblePost(post, userPayload, blocks, files)).toEqual(mockPostDto());
    });
  });

  describe('assemblePost()', () => {
    it('여러 게시글, 블록, 파일, 사용자 정보를 받아 계층적 구조로 조립할 수 있다.', () => {
      const post = mockPost();
      const userPayload = mockUserPayload();
      const blocks = mockBlocks();
      const files = mockFiles();

      expect(service.assemblePosts([post], [userPayload], blocks, files)).toEqual([mockPostDto()]);
    });
  });
});
