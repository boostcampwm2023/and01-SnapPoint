import { Test, TestingModule } from '@nestjs/testing';
import { FileApiController } from './file-api.controller';
import { FileApiService } from './file-api.service';
import { BucketService } from '@/common/bucket.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaService } from '@/common/prisma/prisma.service';
import { FileService } from '@/domain/file/file.service';
import { ConfigService } from '@nestjs/config';

describe('FileApiController', () => {
  let controller: FileApiController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [FileApiController],
      providers: [BucketService, PrismaService, PrismaProvider, FileApiService, FileService, ConfigService],
    }).compile();

    controller = module.get<FileApiController>(FileApiController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
