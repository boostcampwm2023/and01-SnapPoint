import { ConflictException, Injectable, UnauthorizedException } from '@nestjs/common';
import { UserService } from '@/domain/user/user.service';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { TokenService } from '@/domain/token/token.service';
import { LoginAuthDto } from './dto/login-auth.dto';
import { LoginDto } from './dto/login.dto';
import { CreateAuthDto } from './dto/create-auth.dto';
import { UserDto } from './dto/user.dto';

@Injectable()
export class AuthService {
  constructor(
    readonly userService: UserService,
    readonly jwtService: JwtService,
    readonly configService: ConfigService,
    readonly tokenService: TokenService,
  ) {}

  async signUp(createAuthDto: CreateAuthDto) {
    const { email } = createAuthDto;
    const user = await this.userService.findUserByEmail({ email });

    if (user) {
      throw new ConflictException('이미 존재하는 이메일입니다.');
    }

    const newUser = await this.userService.createUser(createAuthDto);

    return SignUpDto.of(newUser);
  }

  async validateUser(loginAuthDto: LoginAuthDto) {
    const { email, password } = loginAuthDto;

    const user = await this.userService.findUserByEmail({ email });

    if (!user) {
      throw new NotFoundException('해당 유저가 존재하지 않습니다.');
    }

    const isValidPassword = await this.userService.verifyPassword({ password, hashedPassword: user.password });

    if (!isValidPassword) {
      throw new UnauthorizedException('아이디 또는 비밀번호가 다릅니다.');
    }

    const [accessToken, refreshToken] = await Promise.all([
      this.tokenService.generateAccessToken(user),
      this.tokenService.generateRefreshToken(user),
    ]);

    await this.tokenService.saveRefreshToken({
      userUuid: user.uuid,
      token: refreshToken,
    });

    return LoginDto.of(accessToken, refreshToken);
  }

  async signOut(refreshToken: string) {
    const decodedRefreshToken = await this.jwtService.verifyAsync(refreshToken, {
      secret: this.configService.getOrThrow<string>('JWT_REFRESH_SECRET'),
    });

    const { uuid: userUuid } = decodedRefreshToken;

    await this.tokenService.deleteRefreshToken({ userUuid });
  }
}
