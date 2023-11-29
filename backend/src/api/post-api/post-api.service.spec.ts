import { Test, TestingModule } from '@nestjs/testing';
import { PostApiService } from './post-api.service';

describe('PostApiService', () => {
  let service: PostApiService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [PostApiService],
    }).compile();

    service = module.get<PostApiService>(PostApiService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
