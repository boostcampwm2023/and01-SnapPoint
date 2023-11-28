import { BadRequestException, Injectable, NotFoundException } from '@nestjs/common';
import { UserService } from '@/domain/user/user.service';
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { RefreshTokenService } from '@/domain/refresh-token/refresh-token.service';
import { RefreshTokenDto } from './dto/refresh-auth.dto';
import { LoginAuthDto } from './dto/login-auth.dto';
import { LoginDto } from './dto/login.dto';
import { RefreshDto } from './dto/refresh.dto';

@Injectable()
export class AuthService {
  constructor(
    readonly userService: UserService,
    readonly jwtService: JwtService,
    readonly configService: ConfigService,
    readonly refreshTokenService: RefreshTokenService,
  ) {}

  async validateUser(loginAuthDto: LoginAuthDto) {
    const user = await this.userService.findUserByUniqueInput({ email: loginAuthDto.email });

    if (!user) {
      throw new NotFoundException('해당 유저가 존재하지 않습니다.');
    }

    await this.verifyPassword(loginAuthDto.password, user.password);
    const accessToken = await this.refreshTokenService.generateAccessToken(user);
    const refreshToken = await this.refreshTokenService.generateRefreshToken(user);
    await this.setCurrentRefreshToken(refreshToken, user.uuid);

    return LoginDto.of(accessToken, refreshToken);
  }

  async verifyPassword(plainText: string, hash: string) {
    const isPasswordMatching = await bcrypt.compare(plainText, hash);
    if (!isPasswordMatching) {
      throw new BadRequestException('잘못된 비밀번호입니다.');
    }
  }

  async refresh(refreshTokenDto: RefreshTokenDto): Promise<{ accessToken: string }> {
    const { refreshToken } = refreshTokenDto;

    const decodedRefreshToken = await this.jwtService.verifyAsync(refreshToken, {
      secret: this.configService.get<string>('JWT_REFRESH_SECRET'),
    });

    const refreshTokenEntity = await this.refreshTokenService.findRefreshTokenByUnique({
      userUuid: decodedRefreshToken.uuid,
    });

    if (!refreshTokenEntity) {
      throw new NotFoundException('리프레시 토큰이 존재하지 않습니다.');
    }

    const user = await this.userService.findUserByUniqueInput({ uuid: refreshTokenEntity.userUuid });

    if (!user) {
      throw new NotFoundException('해당 유저가 존재하지 않습니다.');
    }

    const accessToken = await this.refreshTokenService.generateAccessToken(user);

    return RefreshDto.of(accessToken);
  }

  async setCurrentRefreshToken(refreshToken: string, userUuid: string) {
    const isRefreshToken = await this.refreshTokenService.findRefreshTokenByUnique({ userUuid: userUuid });

    if (!isRefreshToken) {
      const createdRefreshToken = await this.refreshTokenService.create({
        userUuid: userUuid,
        token: refreshToken,
        expiresAt: await this.refreshTokenService.getCurrentRefreshTokenExp(),
      });

      return createdRefreshToken;
    }

    const updatedRefreshToken = this.refreshTokenService.update({
      userUuid: userUuid,
      token: refreshToken,
      expiresAt: await this.refreshTokenService.getCurrentRefreshTokenExp(),
    });

    return updatedRefreshToken;
  }
}
