import { Test, TestingModule } from '@nestjs/testing';
import { FileApiController } from './file-api.controller';
import { FileApiService } from './file-api.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaService } from '@/common/prisma/prisma.service';
import { FileService } from '@/domain/file/file.service';
import { mockPrismaProvider } from '@/common/mocks/mock.prisma';
import { RedisCacheService } from '@/common/redis/redis-cache.service';
import { FileRepository } from '@/domain/file/file.repository';
import { RedisManager } from '@liaoliaots/nestjs-redis';
import { mockDeep } from 'jest-mock-extended';

// 모의 Microservice Client
class MockMicroserviceClient {
  emit() {}
}
describe('FileApiController', () => {
  let controller: FileApiController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [FileApiController],
      providers: [
        PrismaService,
        PrismaProvider,
        FileApiService,
        FileService,
        {
          provide: 'MEDIA_SERVICE',
          useValue: new MockMicroserviceClient(), // 모의 클라이언트 사용
        },
        RedisCacheService,
        RedisManager,
        FileRepository,
      ],
    })
      .overrideProvider(PrismaProvider)
      .useValue(mockPrismaProvider)
      .overrideProvider(RedisManager)
      .useValue(mockDeep<RedisManager>())
      .compile();

    controller = module.get<FileApiController>(FileApiController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
