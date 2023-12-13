import { Module } from '@nestjs/common';
import { TerminusModule } from '@nestjs/terminus';
import { HealthController } from './health.controller';
import { HttpModule } from '@nestjs/axios';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaService } from '@/common/prisma/prisma.service';

@Module({
  imports: [TerminusModule, HttpModule],
  controllers: [HealthController],
  providers: [PrismaProvider, PrismaService],
})
export class HealthModule {}
