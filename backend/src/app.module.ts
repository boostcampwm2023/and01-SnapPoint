import { Module } from '@nestjs/common';
import { BlockModule } from './domain/block/block.module';
import { PostModule } from './domain/post/post.module';
import { FileModule } from './domain/file/file.module';
import { PrismaModule } from './common/prisma/prisma.module';
import { PrismaService } from './common/prisma/prisma.service';
import { PrismaProvider } from './common/prisma/prisma.provider';
import { UserModule } from './domain/user/user.module';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { RefreshTokenService } from './domain/refresh-token/refresh-token.service';
import { RefreshTokenModule } from './domain/refresh-token/refresh-token.module';
import { JwtModule } from '@nestjs/jwt';
import { APP_GUARD, APP_PIPE } from '@nestjs/core';
import { JwtAuthGuard } from './common/guards/jwt-auth.guard';
import { ApiModule } from './api/api.module';
import { validationPipe } from './common/pipes/validation.pipe';
import { RedisModule } from '@liaoliaots/nestjs-redis';
import { RedisCacheModule } from './common/redis/redis-cache.module';
import { HealthModule } from './common/health/health.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      cache: true,
      isGlobal: true,
    }),
    RedisModule.forRootAsync({
      imports: [ConfigModule],
      useFactory: async (configService: ConfigService) => ({
        config: {
          host: configService.getOrThrow('REDIS_HOST'),
          port: configService.getOrThrow('REDIS_PORT'),
          password: configService.getOrThrow('REDIS_PASSWORD'),
        },
      }),
      inject: [ConfigService],
    }),
    BlockModule,
    PostModule,
    UserModule,
    FileModule,
    PrismaModule,
    JwtModule,
    RefreshTokenModule,
    ApiModule,
    RedisCacheModule,
    HealthModule,
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
