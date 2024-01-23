import { Global, Module } from '@nestjs/common';
import { BlockModule } from './domain/block/block.module';
import { PostModule } from './domain/post/post.module';
import { FileModule } from './domain/file/file.module';
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
import { TransactionModule, txExtension } from '@takeny1998/nestjs-prisma-transactional';
import { PRISMA_SERVICE, PrismaService } from './common/databases/prisma.service';
import { UtilityModule } from './common/utility/utility.module';

@Global()
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
    TransactionModule,
    BlockModule,
    PostModule,
    UserModule,
    FileModule,
    JwtModule,
    RefreshTokenModule,
    ApiModule,
    RedisCacheModule,
    HealthModule,
    UtilityModule,
  ],
  providers: [
    RefreshTokenService,
    {
      provide: APP_GUARD,
      useClass: JwtAuthGuard,
    },
    {
      provide: APP_PIPE,
      useValue: validationPipe,
    },
    {
      provide: PRISMA_SERVICE,
      useValue: new PrismaService().$extends(txExtension),
    },
  ],
  exports: [PRISMA_SERVICE],
})
export class AppModule {}
