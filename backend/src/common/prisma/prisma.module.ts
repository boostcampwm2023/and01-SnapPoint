import { Module } from '@nestjs/common';
import { PrismaService } from '@/common/prisma/prisma.service';
import { PrismaProvider } from './prisma.provider';

@Module({
  providers: [PrismaService, PrismaProvider],
  exports: [PrismaProvider],
})
export class PrismaModule {}
