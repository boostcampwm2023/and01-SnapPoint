import { Test, TestingModule } from '@nestjs/testing';
import { ValidationService } from './validation.service';
import { PostService } from '@/domain/post/post.service';
import { FileService } from '@/domain/file/file.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaService } from '@/common/prisma/prisma.service';
import { mockDeep } from 'jest-mock-extended';

describe('ValidationService', () => {
  let service: ValidationService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [ValidationService, FileService, PostService, PrismaProvider, PrismaService],
    })
      .overrideProvider(PrismaProvider)
      .useValue(mockDeep<PrismaProvider>())
      .compile();

    service = module.get<ValidationService>(ValidationService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('validatePost()', () => {});
});
