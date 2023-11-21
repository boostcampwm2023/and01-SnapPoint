import { PrismaProvider } from '@/prisma.service';
import { Module } from '@nestjs/common';

@Module({
  providers: [PrismaProvider],
})
export class BlockFileModule {}
