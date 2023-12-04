import { Module } from '@nestjs/common';
import { FileApiController } from '@/api/file-api/file-api.controller';
import { FileApiService } from '@/api/file-api/file-api.service';
import { PostApiController } from '@/api/post-api/post-api.controller';
import { BucketService } from '@/common/bucket.service';
import { PrismaService } from '@/common/prisma/prisma.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { FileService } from '@/domain/file/file.service';
import { PostApiService } from '@/api/post-api/post-api.service';
import { ValidationService } from './validation/validation.service';
import { BlockService } from '@/domain/block/block.service';
import { PostService } from '@/domain/post/post.service';
import { TransformationService } from './transformation/transformation.service';
import { AuthController } from '@/api/auth/auth.controller';
import { JwtModule } from '@nestjs/jwt';
import { PassportModule } from '@nestjs/passport';
import { ConfigService } from '@nestjs/config';
import { JwtStrategy } from '@/common/strategies/auth.jwt.strategy';
import { RefreshTokenService } from '@/domain/refresh-token/refresh-token.service';
import { UserService } from '@/domain/user/user.service';
import { AuthService } from './auth/auth.service';
import { RedisCacheModule } from '@/common/redis/redis-cache.module';

@Module({
  controllers: [FileApiController, PostApiController, AuthController],
  providers: [
    BucketService,
    PrismaService,
    PrismaProvider,
    FileApiService,
    PostApiService,
    ValidationService,
    FileService,
    BlockService,
    PostService,
    TransformationService,
    ConfigService,
    AuthService,
    UserService,
    JwtStrategy,
    RefreshTokenService,
  ],
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
    RedisCacheModule,
  ],
})
export class ApiModule {}
