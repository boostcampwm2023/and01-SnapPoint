import { Module } from '@nestjs/common';
import { PrismaService } from '../prisma.service';
import { PrismaProvider } from './prisma.provider';

@Module({
  providers: [PrismaService, PrismaProvider],
})
export class PrismaModule {}
