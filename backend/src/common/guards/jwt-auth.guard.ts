import { RefreshTokenService } from '@/domain/refresh-token/refresh-token.service';
import { UserService } from '@/domain/user/user.service';
import {
  BadRequestException,
  CanActivate,
  ExecutionContext,
  Injectable,
  InternalServerErrorException,
  UnauthorizedException,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { Reflector } from '@nestjs/core';
import { JwtService } from '@nestjs/jwt';

@Injectable()
export class JwtAuthGuard implements CanActivate {
  constructor(
    private jwtService: JwtService,
    private reflector: Reflector,
    private configService: ConfigService,
    private refreshTokenService: RefreshTokenService,
    private userService: UserService,
  ) {}

  async canActivate(context: ExecutionContext): Promise<any> {
    const isPublic = this.reflector.get<boolean>('isPublic', context.getHandler());

    if (isPublic) {
      return true;
    }

    const request = context.switchToHttp().getRequest();

    try {
      const accessToken = request.cookies['access_token'];

      if (!accessToken) {
        throw new BadRequestException('엑세스 토큰이 존재하지 않습니다.');
      }

      const user = await this.jwtService.verifyAsync(accessToken, {
        secret: this.configService.get<string>('JWT_ACCESS_SECRET'),
      });

      request.user = user;

      return true;
    } catch (err) {
      if (err instanceof BadRequestException) {
        throw err;
      }
      if (!err || err.name !== 'TokenExpiredError') {
        throw new InternalServerErrorException();
      }

      const refreshToken = request.cookies['refresh_token'];

      if (!refreshToken) {
        throw new BadRequestException('리프레시 토큰이 존재하지 않습니다.');
      }

      return this.checkRefreshToken(context, refreshToken);
    }
  }

  async checkRefreshToken(context: ExecutionContext, refreshToken: string) {
    try {
      const decoded = await this.jwtService.verifyAsync(refreshToken, {
        secret: this.configService.get<string>('JWT_REFRESH_SECRET'),
      });

      const refreshTokenEntity = await this.refreshTokenService.findRefreshTokenByUnique({
        userUuid: decoded.uuid,
      });

      if (!refreshTokenEntity) {
        throw new UnauthorizedException('해당 리프레시 토큰이 존재하지 않습니다.');
      }

      const user = await this.userService.findUserByUniqueInput({
        uuid: refreshTokenEntity.userUuid,
      });

      if (!user) {
        throw new UnauthorizedException();
      }

      const newAccessToken = await this.refreshTokenService.generateAccessToken(user);

      context.switchToHttp().getRequest().user = decoded;

      const res = context.switchToHttp().getResponse();
      res.setHeader('Authorization', 'Bearer ' + [newAccessToken, refreshToken]);
      res.cookie('access_token', newAccessToken, {
        httpOnly: true,
      });
      res.cookie('refresh_token', refreshToken, {
        httpOnly: true,
      });
      return true;
    } catch (refreshTokenError) {
      if (refreshTokenError.name === 'TokenExpiredError') {
        throw new UnauthorizedException('리프레시 토큰이 만료되었습니다.');
      }
      return false;
    }
  }
}
