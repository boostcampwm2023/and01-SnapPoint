import { Test, TestingModule } from '@nestjs/testing';
import { PostApiController } from './post-api.controller';
import { PostApiService } from './post-api.service';

describe('PostApiController', () => {
  let controller: PostApiController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [PostApiController],
      providers: [PostApiService],
    }).compile();

    controller = module.get<PostApiController>(PostApiController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
