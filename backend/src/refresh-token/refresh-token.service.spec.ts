import { Test, TestingModule } from '@nestjs/testing';
import { RefreshTokenService } from './refresh-token.service';
import { mockDeep, DeepMockProxy } from 'jest-mock-extended';
import { PrismaProvider } from '@/prisma.service';
import { PrismaClient, RefreshToken } from '@prisma/client';
import { CreateRefreshTokenDto } from './dto/create-refresh-token.dto';

describe('RefreshTokenService', () => {
  let service: RefreshTokenService;
  let prisma: DeepMockProxy<PrismaClient>;

  beforeEach(async () => {
    prisma = mockDeep<PrismaClient>();

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        RefreshTokenService,
        {
          provide: PrismaProvider,
          useValue: {
            get: () => prisma,
          },
        },
      ],
    }).compile();

    service = module.get<RefreshTokenService>(RefreshTokenService);
  });

  describe('리프레쉬 토큰 생성', () => {
    const expiresAt = new Date();
    const createRefreshTokenDtoMock: CreateRefreshTokenDto = {
      userUuid: 'testUuid',
      token: 'testToken',
      expiresAt: expiresAt,
    };
    const refreshTokenMock: RefreshToken = {
      id: 0,
      userUuid: 'testUuid',
      token: 'testToken',
      expiresAt: expiresAt,
      createdAt: new Date(),
      modifiedAt: new Date(),
    };

    it('정상 입력인 경우 특정 유저의 리프레쉬 토큰을 생성한다.', async () => {
      prisma.refreshToken.create.mockResolvedValueOnce(refreshTokenMock);

      const result = await service.create(createRefreshTokenDtoMock);

      expect(result).toEqual(refreshTokenMock);
    });
  });

  describe('리프레쉬 토큰 업데이트', () => {
    const expiresAt = new Date();
    const updatedRefreshTokenDtoMock: CreateRefreshTokenDto = {
      userUuid: 'testUuid',
      token: 'testToken',
      expiresAt: expiresAt,
    };
    const refreshTokenMock: RefreshToken = {
      id: 0,
      userUuid: 'testUuid',
      token: 'testToken',
      expiresAt: expiresAt,
      createdAt: new Date(),
      modifiedAt: new Date(),
    };

    it('정상 입력인 경우 특정 유저의 리프레쉬 토큰을 생성한다.', async () => {
      prisma.refreshToken.update.mockResolvedValueOnce(refreshTokenMock);

      const result = await service.update(updatedRefreshTokenDtoMock);

      expect(result).toEqual(refreshTokenMock);
    });
  });
});
