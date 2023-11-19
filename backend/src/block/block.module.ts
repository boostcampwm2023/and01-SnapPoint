import { Module } from '@nestjs/common';
import { BlockService } from './block.service';
import { PrismaProvider } from '@/prisma.service';

@Module({
  providers: [BlockService, PrismaProvider],
})
export class BlocksModule {}
