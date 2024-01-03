import { Test, TestingModule } from '@nestjs/testing';
import { RefreshTokenService } from './refresh-token.service';
import { mockDeep, DeepMockProxy } from 'jest-mock-extended';
import { RefreshToken } from '@prisma/client';
import { CreateRefreshTokenDto } from './dto/create-refresh-token.dto';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { PRISMA_SERVICE, PrismaService } from '@/common/databases/prisma.service';

jest.mock('@nestjs/jwt');
jest.mock('@nestjs/config');

describe('RefreshTokenService', () => {
  let service: RefreshTokenService;
  let prisma: DeepMockProxy<PrismaService>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        RefreshTokenService,
        JwtService,
        ConfigService,
        {
          provide: PRISMA_SERVICE,
          useValue: mockDeep<PrismaService>(),
        },
      ],
    }).compile();

    service = module.get<RefreshTokenService>(RefreshTokenService);
    prisma = module.get(PRISMA_SERVICE);
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
      prisma.refreshToken.findUnique.mockResolvedValueOnce(refreshTokenMock);

      const result = await service.update(updatedRefreshTokenDtoMock);

      expect(result).toEqual(refreshTokenMock);
    });
  });
});
