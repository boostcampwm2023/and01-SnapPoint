import { PrismaService } from '@/prisma.service';
import { PrismaModule } from '@/prisma/prisma.module';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { Module } from '@nestjs/common';

@Module({
  imports: [PrismaModule],
  providers: [PrismaProvider, PrismaService],
})
export class BlockFileModule {}
