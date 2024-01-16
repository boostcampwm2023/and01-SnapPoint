import { Test, TestingModule } from '@nestjs/testing';
import { TransformationService } from './transformation.service';
import { mockDecomposedImagePostDto, mockDecomposedVideoPostDto } from './mocks/mock.dto.decomposed-post';
import { mockImagePostDto, mockVideoPostDto } from './mocks/mock.dto.modify-post';

describe('TransformationService', () => {
  let service: TransformationService;

  const postUuid = 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e';

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [TransformationService],
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
});
