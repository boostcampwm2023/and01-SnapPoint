import { ConflictException, Injectable, UnauthorizedException } from '@nestjs/common';
import { UserService } from '@/domain/user/user.service';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { TokenService } from '@/domain/token/token.service';
import { SignInDto } from './dto/sign-in.dto';
import { TokenDto } from './dto/token';
import { SignUpDto } from './dto/sign-up.dto';
import { UserDto } from './dto/user.dto';

@Injectable()
export class AuthService {
  constructor(
    readonly userService: UserService,
    readonly jwtService: JwtService,
    readonly configService: ConfigService,
    readonly tokenService: TokenService,
  ) {}

  async signUp(SignUpDto: SignUpDto) {
    const { email } = SignUpDto;
    const user = await this.userService.findUserByEmail({ email });

    if (user) {
      throw new ConflictException('이미 존재하는 이메일입니다.');
    }

    const newUser = await this.userService.createUser(SignUpDto);

    return UserDto.of(newUser);
  }

  async signIn(SignInDto: SignInDto) {
    const { email, password } = SignInDto;

    const user = await this.userService.findUserByEmail({ email });

    if (!user) {
      throw new UnauthorizedException('아이디 또는 비밀번호가 다릅니다.');
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

    return TokenDto.of(accessToken, refreshToken);
  }

  async signOut(refreshToken: string) {
    const decodedRefreshToken = await this.jwtService.verifyAsync(refreshToken, {
      secret: this.configService.getOrThrow<string>('JWT_REFRESH_SECRET'),
    });

    const { uuid: userUuid } = decodedRefreshToken;

    await this.tokenService.deleteRefreshToken({ userUuid });
  }
}
