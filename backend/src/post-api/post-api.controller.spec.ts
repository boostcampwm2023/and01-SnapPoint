import { Test, TestingModule } from '@nestjs/testing';
import { PostApiController } from './post-api.controller';
import { BlockService } from '@/block/block.service';
import { PrismaService } from '@/prisma.service';
import { PrismaModule } from '@/prisma/prisma.module';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { mockDeep } from 'jest-mock-extended';

describe('PostApiController', () => {
  let controller: PostApiController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [PrismaModule, PostApiController],
      providers: [BlockService, PrismaService, PrismaProvider],
    })
      .overrideProvider(PrismaService)
      .useValue(mockDeep<PrismaService>())
      .compile();

    controller = module.get<PostApiController>(PostApiController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
