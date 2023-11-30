import { Test, TestingModule } from '@nestjs/testing';
import { TransformationService } from './transformation.service';

describe('TransformationService', () => {
  let service: TransformationService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [TransformationService],
    }).compile();

    service = module.get<TransformationService>(TransformationService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
