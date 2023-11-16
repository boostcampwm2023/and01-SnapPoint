import { Test, TestingModule } from '@nestjs/testing';
import { BlockFileService } from './block-file.service';

describe('BlockFilesService', () => {
  let service: BlockFileService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [BlockFileService],
    }).compile();

    service = module.get<BlockFileService>(BlockFileService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
