import { PrismaService } from '@/common/prisma/prisma.service';
import { Module } from '@nestjs/common';
import { BlockService } from './block.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaModule } from '@/common/prisma/prisma.module';

@Module({
  imports: [PrismaModule],
  providers: [BlockService, PrismaService, PrismaProvider],
})
export class BlocksModule {}
