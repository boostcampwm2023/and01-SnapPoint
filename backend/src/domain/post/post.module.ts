import { Module } from '@nestjs/common';
import { PostService } from './post.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaModule } from '@/common/prisma/prisma.module';
import { PrismaService } from '@/common/prisma/prisma.service';

@Module({
  imports: [PrismaModule],
  providers: [PostService, PrismaService, PrismaProvider],
})
export class PostModule {}
