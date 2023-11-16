import { Module } from '@nestjs/common';
import { BlockService } from './block.service';
import { PrismaService } from '@/prisma.service';

@Module({
  providers: [BlockService, PrismaService],
})
export class BlocksModule {}
