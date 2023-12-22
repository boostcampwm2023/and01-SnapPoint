import { Test, TestingModule } from '@nestjs/testing';
import { ValidationService } from './validation.service';
import { FileService } from '@/domain/file/file.service';
import { mockDeep } from 'jest-mock-extended';
import { BadRequestException } from '@nestjs/common';
import { ValidateFileDto } from './dtos/validate-file.dto';
import { ValidateBlockDto } from './dtos/validate-block.dto';
import { TxPrismaService } from '@/common/transaction/tx-prisma.service';

describe('ValidationService', () => {
  let service: ValidationService;
  // let fileService: DeepMockProxy<FileService>;
  let mockFileDto: ValidateFileDto;
  let mockBlockDto: ValidateBlockDto;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [ValidationService, FileService, TxPrismaService],
    })
      .overrideProvider(TxPrismaService)
      .useValue(mockDeep<TxPrismaService>())
      .overrideProvider(FileService)
      .useValue(mockDeep<FileService>())
      .compile();

    service = module.get<ValidationService>(ValidationService);
    // fileService = module.get(FileService);

    mockFileDto = {
      uuid: 'mock-file-uuid-1',
      source: 'block',
      sourceUuid: 'mock-block-uuid-1',
    };
    mockBlockDto = {
      uuid: 'mock-block-uuid-1',
      type: 'text',
      latitude: 8.1414,
      longitude: -74.3538,
    };
  });

  // describe('validateFile()', () => {
  //   it('업로드된 파일이 없을 경우 BadRequestException을 발생시킨다', async () => {
  //     fileService.findFiles.mockResolvedValue([]);
  //     await expect(service.validateFiles([mockFileDto], 'mock-user-uuid')).rejects.toThrow(BadRequestException);
  //   });

  //   it('자신이 업로드하지 않은 파일일 경우 ForbiddenException을 발생시킨다', async () => {
  //     fileService.findFiles.mockResolvedValue([
  //       {
  //         id: 1,
  //         uuid: 'mock-file-uuid-1',
  //         userUuid: 'mock-user-uuid',
  //         mimeType: 'image/jpeg',
  //         url: 'https://mock.storage.com/mock-file-uuid-1',
  //         createdAt: new Date('2023-11-23T15:02:10.626Z'),
  //         isDeleted: false,
  //         source: null,
  //         sourceUuid: null,
  //         isProcessed: false,
  //       },
  //     ]);

  //     await expect(service.validateFiles([mockFileDto], 'not-exist-user-uuid')).rejects.toThrow(ForbiddenException);
  //   });
  // });

  describe('validateBlocks()', () => {
    it('텍스트 타입에 위도 및 경도 값이 포함된 경우 BadRequestException을 발생시킨다', async () => {
      await expect(service.validateBlocks([mockBlockDto], [mockFileDto])).rejects.toThrow(
        new BadRequestException('Latitude and longitude should not be provided for text type'),
      );
    });

    it('미디어 타입에 위도 및 경도 값이 없는 경우 BadRequestException을 발생시킨다', async () => {
      mockBlockDto.type = 'media';
      delete mockBlockDto.latitude;
      delete mockBlockDto.longitude;

      await expect(service.validateBlocks([mockBlockDto], [mockFileDto])).rejects.toThrow(
        new BadRequestException('Latitude and longitude should be provided for media type'),
      );
    });

    it('텍스트 타입에 연관된 파일이 있는 경우 BadRequestException을 발생시킨다', async () => {
      mockBlockDto.type = 'text';
      delete mockBlockDto.latitude;
      delete mockBlockDto.longitude;

      await expect(service.validateBlocks([mockBlockDto], [mockFileDto])).rejects.toThrow(
        new BadRequestException('File block must not have media file'),
      );
    });

    it('미디어 타입에 연관된 파일이 없는 경우 BadRequestException을 발생시킨다', async () => {
      mockBlockDto.type = 'media';
      mockBlockDto.latitude = 8.1414;
      mockBlockDto.longitude = -74.3538;
      mockFileDto.sourceUuid = 'not-exist-block-uuid';

      await expect(service.validateBlocks([mockBlockDto], [mockFileDto])).rejects.toThrow(
        new BadRequestException('Media Block must have at least one files'),
      );
    });
  });
});
