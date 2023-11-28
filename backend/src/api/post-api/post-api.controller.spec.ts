import { Test, TestingModule } from '@nestjs/testing';
import { PostApiController } from './post-api.controller';
import { PostApiService } from './post-api.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaService } from '@/common/prisma/prisma.service';
import { BlockService } from '@/domain/block/block.service';
import { FileService } from '@/domain/file/file.service';
import { PostService } from '@/domain/post/post.service';
import { ValidationService } from '../validation/validation.service';

describe('PostApiController', () => {
  let controller: PostApiController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [PostApiController],
      providers: [
        PostApiService,
        PostApiService,
        PrismaService,
        PrismaProvider,
        ValidationService,
        PostService,
        BlockService,
        FileService,
      ],
    }).compile();

    controller = module.get<PostApiController>(PostApiController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
