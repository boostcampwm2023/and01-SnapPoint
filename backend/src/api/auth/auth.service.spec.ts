import { DeepMockProxy, mockDeep } from 'jest-mock-extended';
import { Test, TestingModule } from '@nestjs/testing';
import { AuthService } from './auth.service';
import { UserService } from '@/domain/user/user.service';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { RefreshTokenService } from '@/domain/refresh-token/refresh-token.service';
import { LoginAuthDto } from './dto/login-auth.dto';
import { RefreshToken, User } from '@prisma/client';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaService } from '@/common/prisma/prisma.service';

describe('AuthSerivce', () => {
  let service: AuthService;
  let jwtService: DeepMockProxy<JwtService>;
  let refreshTokenService: DeepMockProxy<RefreshTokenService>;
  let userService: DeepMockProxy<UserService>;

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
    })
      .overrideProvider(JwtService)
      .useValue(mockDeep<JwtService>())
      .overrideProvider(RefreshTokenService)
      .useValue(mockDeep<RefreshTokenService>())
      .overrideProvider(UserService)
      .useValue(mockDeep<UserService>())
      .compile();

    service = module.get<AuthService>(AuthService);
    jwtService = module.get(JwtService);
    refreshTokenService = module.get(RefreshTokenService);
    userService = module.get(UserService);
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
      userService.findUserByUniqueInput.mockResolvedValueOnce(userMock);
      refreshTokenService.generateAccessToken.mockResolvedValueOnce(access_token);
      refreshTokenService.generateRefreshToken.mockResolvedValueOnce(refresh_token);

      service.verifyPassword;

      const result = await service.validateUser(loginAuthDto);
      expect(result).toEqual({
        accessToken: access_token,
        refreshToken: refresh_token,
      });
    });
  });

  describe('refresh', () => {
    const access_token = 'test_access_token';
    const refreshToken = 'test_refresh_token';
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
      jwtService.verifyAsync.mockResolvedValue({ uuid: 'testUuid' });
      userService.findUserByUniqueInput.mockResolvedValueOnce(userMock);
      refreshTokenService.generateAccessToken.mockResolvedValueOnce(access_token);
      refreshTokenService.findRefreshTokenByUnique.mockResolvedValue(refreshTokenMock);

      const result = await service.refresh(refreshToken);

      expect(result).toEqual({
        accessToken: access_token,
      });
    });
  });
});
