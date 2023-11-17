import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { PrismaService } from './prisma.service';
import { BlocksModule } from './block/block.module';
import { PostModule } from './post/post.module';
import { BlockFileModule } from './block-file/block-file.module';
import { PostApiModule } from './post-api/post-api.module';

@Module({
  imports: [BlocksModule, PostModule, BlockFileModule, PostApiModule],
  controllers: [AppController],
  providers: [AppService, PrismaService],
})
export class AppModule {}
