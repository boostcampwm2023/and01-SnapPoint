import { mockDeep } from 'jest-mock-extended';
import { Test, TestingModule } from '@nestjs/testing';
import { FileApiService } from './file-api.service';
// import { ForbiddenException, NotFoundException } from '@nestjs/common';
import { FileService } from '@/domain/file/file.service';
import { mockPrismaProvider } from '@/common/mocks/mock.prisma';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaService } from '@/common/prisma/prisma.service';
import { RedisCacheService } from '@/common/redis/redis-cache.service';
import { RedisManager } from '@liaoliaots/nestjs-redis';
import { FileRepository } from '@/domain/file/file.repository';
// import { mockFileEntities } from '@/common/mocks/mock.entites.file';
// import { FileDto } from './dto/file.dto';

describe('FileApiService', () => {
  let service: FileApiService;
  // let fileService: DeepMockProxy<FileService>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        PrismaService,
        PrismaProvider,
        FileApiService,
        FileService,
        RedisCacheService,
        RedisManager,
        FileRepository,
      ],
    })
      .overrideProvider(PrismaProvider)
      .useValue(mockPrismaProvider)
      .overrideProvider(FileService)
      .useValue(mockDeep<FileService>())
      .overrideProvider(RedisManager)
      .useValue(mockDeep<RedisManager>())
      .compile();

    service = module.get<FileApiService>(FileApiService);
    // fileService = module.get(FileService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
  // describe('findFile()', () => {
  //   it('해당하는 파일 정보를 찾고, DTO로 변환해 반환한다.', async () => {
  //     fileService.findFilesById.mockResolvedValue(mockFileEntities[0]);
  //     await expect(service.findFile('mock-file-uuid-1', 'mock-user-uuid')).resolves.toEqual(
  //       FileDto.of(mockFileEntities[0]),
  //     );
  //   });

  //   it('파일을 찾을 수 없는 경우 NotFoundException을 발생한다.', async () => {
  //     fileService.findFile.mockResolvedValue(null);
  //     await expect(service.findFile('not-exist-uuid', 'mock-user-uuid')).rejects.toThrow(NotFoundException);
  //   });

  //   it('파일을 찾았지만 해당 사용자가 업로드한 파일이 아니면 ForbiddenExpcetion을 발생한다.', async () => {
  //     fileService.findFile.mockResolvedValue(mockFileEntities[0]);
  //     await expect(service.findFile('mock-file-uuid-1', 'not-exist-user-uuid')).rejects.toThrow(ForbiddenException);
  //   });
  // });
});
