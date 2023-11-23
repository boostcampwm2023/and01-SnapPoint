import { PrismaService } from '@/prisma.service';
import { PrismaModule } from '@/prisma/prisma.module';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { Module } from '@nestjs/common';
import { BlockFileService } from './block-file.service';

@Module({
  imports: [PrismaModule],
  providers: [PrismaProvider, PrismaService, BlockFileService],
})
export class BlockFileModule {}
