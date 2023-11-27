import { ExtractJwt, Strategy } from 'passport-jwt';
import { PassportStrategy } from '@nestjs/passport';
import { Injectable, UnauthorizedException } from '@nestjs/common';
import { UserService } from '@/user/user.service';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(
    readonly userService: UserService,
    readonly configService: ConfigService,
  ) {
    super({
      // Authorization에서 Bearer Token에 JWT 토큰을 담아 전송
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      // Passport에 검증 위임
      ignoreExpiration: false,
      secretOrKey: configService.get<string>('JWT_ACCESS_SECRET'),
    });
  }

  async validate(payload: { uuid: string }) {
    const { uuid } = payload;

    if (!uuid) {
      throw new UnauthorizedException();
    }

    return uuid;
  }
}
