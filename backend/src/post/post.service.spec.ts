import { Test, TestingModule } from '@nestjs/testing';
import { PostService } from './post.service';
import { PrismaModule } from '@/prisma/prisma.module';
import { BlockService } from '@/block/block.service';
import { PrismaService } from '@/prisma.service';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { mockDeep } from 'jest-mock-extended';

describe('PostsService', () => {
  let service: PostService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [PrismaModule],
      providers: [PostService, PrismaService, PrismaProvider, BlockService],
    })
      .overrideProvider(PrismaService)
      .useValue(mockDeep<PrismaService>())
      .compile();
    service = module.get<PostService>(PostService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
