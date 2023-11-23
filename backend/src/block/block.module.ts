import { PrismaService } from '@/prisma.service';
import { Module } from '@nestjs/common';
import { BlockService } from './block.service';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { PrismaModule } from '@/prisma/prisma.module';

@Module({
  imports: [PrismaModule],
  providers: [BlockService, PrismaService, PrismaProvider],
})
export class BlocksModule {}
