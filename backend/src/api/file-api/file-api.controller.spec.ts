import { Test, TestingModule } from '@nestjs/testing';
import { FileApiController } from './file-api.controller';
import { FileApiService } from './file-api.service';

describe('FileApiController', () => {
  let controller: FileApiController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [FileApiController],
      providers: [FileApiService],
    }).compile();

    controller = module.get<FileApiController>(FileApiController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
