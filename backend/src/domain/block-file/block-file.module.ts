import { PrismaService } from '@/common/prisma/prisma.service';
import { PrismaModule } from '@/common/prisma/prisma.module';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { Module } from '@nestjs/common';
import { BlockFileService } from './block-file.service';

@Module({
  imports: [PrismaModule],
  providers: [PrismaProvider, PrismaService, BlockFileService],
})
export class BlockFileModule {}
