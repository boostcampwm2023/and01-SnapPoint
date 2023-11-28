import { Test, TestingModule } from '@nestjs/testing';
import { AuthService } from './auth.service';
import { UserService } from '@/domain/user/user.service';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { RefreshTokenService } from '@/domain/refresh-token/refresh-token.service';
import { LoginAuthDto } from './dto/login-auth.dto';
import { RefreshToken, User } from '@prisma/client';
import { RefreshTokenDto } from './dto/refresh-auth.dto';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaService } from '@/common/prisma/prisma.service';

jest.mock('@nestjs/jwt');
jest.mock('@nestjs/config');
jest.mock('@/refresh-token/refresh-token.service');
jest.mock('@/user/user.service');

describe('AuthSerivce', () => {
  let service: AuthService;
  let jwtService: JwtService;
  let refreshTokenService: RefreshTokenService;
  let userService: UserService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,
        UserService,
        ConfigService,
        JwtService,
        RefreshTokenService,
        PrismaProvider,
        PrismaService,
      ],
    }).compile();

    service = module.get<AuthService>(AuthService);
    jwtService = module.get<JwtService>(JwtService);
    refreshTokenService = module.get<RefreshTokenService>(RefreshTokenService);
    userService = module.get<UserService>(UserService);
  });

  describe('로그인: validateUser()', () => {
    const loginAuthDto: LoginAuthDto = {
      email: 'test@example.com',
      password: 'password123',
    };
    const access_token = 'test_token';
    const refresh_token = 'test_token';
    const userMock: User = {
      id: 1,
      email: loginAuthDto.email,
      password: loginAuthDto.password,
      nickname: 'testNickname',
      uuid: 'testUuid',
      createdAt: new Date(),
      modifiedAt: new Date(),
      isDeleted: false,
    };
    const refreshToken: RefreshToken = {
      id: 0,
      userUuid: 'testUuid',
      token: 'testtoken',
      expiresAt: new Date(),
      createdAt: new Date(),
      modifiedAt: new Date(),
    };

    it('존재하는 유저의 경우 accessToken, refreshToken을 반환한다.', async () => {
      jest.spyOn(service, 'verifyPassword').mockResolvedValueOnce();
      jest.spyOn(service, 'setCurrentRefreshToken').mockResolvedValueOnce(refreshToken);
      jest.spyOn(userService, 'findUserByUniqueInput').mockResolvedValueOnce(userMock);
      jest.spyOn(refreshTokenService, 'generateAccessToken').mockResolvedValueOnce(access_token);
      jest.spyOn(refreshTokenService, 'generateRefreshToken').mockResolvedValueOnce(refresh_token);

      const result = await service.validateUser(loginAuthDto);
      expect(result).toEqual({
        accessToken: access_token,
        refreshToken: refresh_token,
      });
    });
  });

  describe('refresh', () => {
    const refreshTokenDto: RefreshTokenDto = {
      refreshToken: 'test_refresh_token',
    };
    const access_token = 'test_access_token';
    const userMock: User = {
      id: 1,
      email: 'test@example.com',
      password: 'Password123@@',
      nickname: 'testNickname',
      uuid: 'testUuid',
      createdAt: new Date(),
      modifiedAt: new Date(),
      isDeleted: false,
    };
    const refreshTokenMock: RefreshToken = {
      id: 0,
      userUuid: 'testUuid',
      token: 'testtoken',
      expiresAt: new Date(),
      createdAt: new Date(),
      modifiedAt: new Date(),
    };

    it('refresh 토큰 검증 이후 access_token을 반환한다.', async () => {
      jest.spyOn(jwtService, 'verifyAsync').mockResolvedValue({ uuid: 'testUuid' });
      jest.spyOn(userService, 'findUserByUniqueInput').mockResolvedValueOnce(userMock);
      jest.spyOn(refreshTokenService, 'generateAccessToken').mockResolvedValueOnce(access_token);
      jest.spyOn(refreshTokenService, 'findRefreshTokenByUnique').mockResolvedValueOnce(refreshTokenMock);

      const result = await service.refresh(refreshTokenDto);

      expect(result).toEqual({
        accessToken: access_token,
      });
    });
  });
});
