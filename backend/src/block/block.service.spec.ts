import { PrismaService } from './../prisma.service';
import { Test, TestingModule } from '@nestjs/testing';
import { BlockService } from './block.service';
import { mockDeep } from 'jest-mock-extended';
import { BadRequestException } from '@nestjs/common';
import { PrismaModule } from '@/prisma/prisma.module';
import { PrismaProvider } from '@/prisma/prisma.provider';

describe('BlocksService', () => {
  let service: BlockService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [PrismaModule],
      providers: [BlockService, PrismaService, PrismaProvider],
    })
      .overrideProvider(PrismaService)
      .useValue(mockDeep<PrismaService>())
      .compile();

    service = module.get<BlockService>(BlockService);
  });

  describe('create', () => {
    it('텍스트 블록은 위도, 경도 값을 가질 수 없다.', async () => {
      await expect(
        service.create('589bb04b-9aa3-4a2b-bc66-8ea8235c4c01', {
          type: 'text',
          content: 'This is Text Block.',
          latitude: 8.1414,
          longitude: -74.3538,
          order: 1,
        }),
      ).rejects.toThrow(BadRequestException);
    });
  });
});
