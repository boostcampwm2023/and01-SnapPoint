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

@Module({
  imports: [BlocksModule, PostModule, BlockFileModule, PostApiModule, FileModule, PrismaModule, UserModule, AuthModule],
  controllers: [],
  providers: [PrismaService, PrismaProvider],
})
export class AppModule {}
