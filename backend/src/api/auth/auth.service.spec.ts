import { DeepMockProxy, mockDeep } from 'jest-mock-extended';
import { Test, TestingModule } from '@nestjs/testing';
import { AuthService } from './auth.service';
import { UserService } from '@/domain/user/user.service';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { TokenService } from '@/domain/token/token.service';
import { PRISMA_SERVICE, PrismaService } from '@/common/databases/prisma.service';
import { ConflictException, UnauthorizedException } from '@nestjs/common';
import { mockAccessToken, mockRefreshToken, mockUser } from './mocks/mock.entity.auth';
import { mockSignInDto, mockSignUpDto, mockUserDto } from './mocks/mock.dto.auth';

describe('AuthSerivce', () => {
  let service: AuthService;
  let tokenService: DeepMockProxy<TokenService>;
  let userService: DeepMockProxy<UserService>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,
        UserService,
        ConfigService,
        JwtService,
        TokenService,
        {
          provide: PRISMA_SERVICE,
          useValue: mockDeep<PrismaService>(),
        },
      ],
    })
      .overrideProvider(JwtService)
      .useValue(mockDeep<JwtService>())
      .overrideProvider(TokenService)
      .useValue(mockDeep<TokenService>())
      .overrideProvider(UserService)
      .useValue(mockDeep<UserService>())
      .compile();

    service = module.get<AuthService>(AuthService);
    tokenService = module.get(TokenService);
    userService = module.get(UserService);
  });

  describe('signIn()', () => {
    it('정상 로그인 시 토큰을 반환한다', async () => {
      const accessToken = mockAccessToken();
      const refreshToken = mockRefreshToken();

      userService.findUserByEmail.mockResolvedValue(mockUser());
      userService.verifyPassword.mockResolvedValue(true);
      tokenService.generateAccessToken.mockResolvedValue(accessToken);
      tokenService.generateRefreshToken.mockResolvedValue(refreshToken);

      const result = await service.signIn(mockSignInDto());

      expect(result).toEqual({ accessToken, refreshToken });
    });

    it('존재하지 않는 사용자일 경우 UnauthorizedException을 발생시킨다', () => {
      userService.findUserByEmail.mockResolvedValueOnce(null);

      expect(service.signIn(mockSignInDto())).rejects.toThrow(
        new UnauthorizedException('아이디 또는 비밀번호가 다릅니다.'),
      );
    });

    it('비밀번호가 다른 경우 UnauthorizedException을 발생한다.', () => {
      userService.findUserByEmail.mockResolvedValueOnce(mockUser());
      userService.verifyPassword.mockResolvedValueOnce(false);

      expect(service.signIn(mockSignInDto())).rejects.toThrow(
        new UnauthorizedException('아이디 또는 비밀번호가 다릅니다.'),
      );
    });
  });

  describe('signUp()', () => {
    it('정상인 경우 새로운 사용자를 생성한다.', async () => {
      userService.findUserByEmail.mockResolvedValueOnce(null);
      userService.createUser.mockResolvedValueOnce(mockUser());

      expect(service.signUp(mockSignUpDto())).resolves.toEqual(mockUserDto());
    });

    it('사용자가 이미 존재하면 ConflictException을 발생한다.', async () => {
      userService.findUserByEmail.mockResolvedValueOnce(mockUser());

      expect(service.signUp(mockSignUpDto())).rejects.toThrow(new ConflictException('이미 존재하는 이메일입니다.'));
    });
  });
});
