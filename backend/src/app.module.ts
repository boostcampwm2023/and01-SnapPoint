import { Module } from '@nestjs/common';
import { BlocksModule } from './domain/block/block.module';
import { PostModule } from './domain/post/post.module';
import { FileModule } from './domain/file/file.module';
import { PrismaModule } from './common/prisma/prisma.module';
import { PrismaService } from './common/prisma/prisma.service';
import { PrismaProvider } from './common/prisma/prisma.provider';
import { UserModule } from './domain/user/user.module';
import { AuthModule } from './api/auth/auth.module';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { RefreshTokenService } from './domain/refresh-token/refresh-token.service';
import { RefreshTokenModule } from './domain/refresh-token/refresh-token.module';
import { JwtModule } from '@nestjs/jwt';
import { APP_GUARD, APP_PIPE } from '@nestjs/core';
import { JwtAuthGuard } from './common/guards/jwt-auth.guard';
import { ApiModule } from './api/api.module';
import { RedisCacheModule } from './common/redis/redis-cache.module';
import { RedisModule } from '@liaoliaots/nestjs-redis';
import { validationPipe } from './common/pipes/validation.pipe';

@Module({
  imports: [
    BlocksModule,
    PostModule,
    UserModule,
    AuthModule,
    FileModule,
    PrismaModule,
    ConfigModule.forRoot({
      cache: true,
      isGlobal: true,
    }),
    JwtModule,
    RefreshTokenModule,
    ApiModule,
    RedisCacheModule,
    RedisModule.forRootAsync({
      imports: [ConfigModule],
      useFactory: async (configService: ConfigService) => ({
        config: {
          host: configService.get('REDIS_HOST'),
          port: configService.get('REDIS_PORT'),
          password: configService.get('REDIS_PASSWORD'),
        },
      }),
      inject: [ConfigService],
    }),
  ],
  controllers: [],
  providers: [
    PrismaService,
    PrismaProvider,
    RefreshTokenService,
    {
      provide: APP_GUARD,
      useClass: JwtAuthGuard,
    },
    {
      provide: APP_PIPE,
      useValue: validationPipe,
    },
  ],
})
export class AppModule {}
