import { Module } from '@nestjs/common';
import { BlocksModule } from './block/block.module';
import { PostModule } from './post/post.module';
import { BlockFileModule } from './block-file/block-file.module';
import { PostApiModule } from './post-api/post-api.module';
import { FileModule } from './file/file.module';
import { PrismaModule } from './prisma/prisma.module';
import { PrismaService } from './prisma.service';
import { PrismaProvider } from './prisma/prisma.provider';
import { UserModule } from './user/user.module';
import { AuthModule } from './auth/auth.module';
import { ConfigModule } from '@nestjs/config';
import { RefreshTokenService } from './refresh-token/refresh-token.service';
import { RefreshTokenModule } from './refresh-token/refresh-token.module';
import { JwtModule } from '@nestjs/jwt';

@Module({
  imports: [
    BlocksModule,
    PostModule,
    BlockFileModule,
    PostApiModule,
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
  ],
  controllers: [],
  providers: [PrismaService, PrismaProvider, RefreshTokenService],
})
export class AppModule {}
