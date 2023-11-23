import { Module } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthController } from './auth.controller';
import { PassportModule } from '@nestjs/passport';
import { JwtModule } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { JwtStrategy } from './strategies/auth.jwt.strategy';
import { RefreshTokenService } from '@/refresh-token/refresh-token.service';
import { UserService } from '@/user/user.service';

@Module({
  controllers: [AuthController],
  providers: [AuthService, UserService, ConfigService, JwtStrategy, ConfigService, RefreshTokenService],
  imports: [
    PassportModule.register({
      defaultStrategy: 'jwt',
      session: false,
    }),
    JwtModule.registerAsync({
      useFactory: async (configService: ConfigService) => ({
        secret: configService.get('JWT_ACCESS_SECRET'),
        signOptions: {
          expiresIn: configService.get('JWT_ACCESS_EXPIRATION_TIME'),
        },
      }),
      inject: [ConfigService],
    }),
  ],
})
export class AuthModule {}
