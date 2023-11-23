import { BadRequestException, Injectable, UnauthorizedException } from '@nestjs/common';
import { UserService } from '@/user/user.service';
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt';
import { LoginAuthDto } from './dto/login-auth.dto';
import { ConfigService } from '@nestjs/config';
import { RefreshTokenService } from '@/refresh-token/refresh-token.service';
import { RefreshTokenDto } from './dto/refresh-auth.dto';

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
      throw new UnauthorizedException();
    }

    await this.verifyPassword(loginAuthDto.password, user.password);
    const accessToken = await this.refreshTokenService.generateAccessToken(user);
    const refreshToken = await this.refreshTokenService.generateRefreshToken(user);
    await this.setCurrentRefreshToken(refreshToken, user.uuid);

    return {
      accessToken: accessToken,
      refreshToken: refreshToken,
    };
  }

  async verifyPassword(plainText: string, hash: string) {
    const isPasswordMatching = await bcrypt.compare(plainText, hash);
    if (!isPasswordMatching) {
      throw new BadRequestException();
    }
  }

  async refresh(refreshTokenDto: RefreshTokenDto): Promise<{ accessToken: string }> {
    const { refresh_token } = refreshTokenDto;

    const decodedRefreshToken = await this.jwtService.verifyAsync(refresh_token, {
      secret: this.configService.get<string>('JWT_REFRESH_SECRET'),
    });

    const refreshToken = await this.refreshTokenService.findRefreshTokenByUnique({
      userUuid: decodedRefreshToken.uuid,
    });

    if (!refreshToken) {
      throw new BadRequestException();
    }

    const user = await this.userService.findUserByUniqueInput({ uuid: refreshToken.userUuid });

    if (!user) {
      throw new BadRequestException();
    }

    const accessToken = await this.refreshTokenService.generateAccessToken(user);

    return { accessToken };
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
