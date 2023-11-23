import { Test, TestingModule } from '@nestjs/testing';
import { FileController } from './file.controller';
import { FileService } from './file.service';
import { BucketService } from '@/bucket.service';
import { PrismaService } from '@/prisma.service';
import { PrismaModule } from '@/prisma/prisma.module';
import { PrismaProvider } from '@/prisma/prisma.provider';

describe('FileController', () => {
  let controller: FileController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [PrismaModule],
      controllers: [FileController],
      providers: [BucketService, PrismaService, PrismaProvider, FileService],
    }).compile();

    controller = module.get<FileController>(FileController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
