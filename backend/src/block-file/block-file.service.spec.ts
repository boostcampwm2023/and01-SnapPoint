import { Test, TestingModule } from '@nestjs/testing';
import { BlockFileService } from './block-file.service';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { PrismaService } from '@/prisma.service';
import { PrismaModule } from '@/prisma/prisma.module';

describe('BlockFilesService', () => {
  let service: BlockFileService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [PrismaModule],
      providers: [PrismaProvider, PrismaService, BlockFileService],
    }).compile();

    service = module.get<BlockFileService>(BlockFileService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
