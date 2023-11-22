import { Module } from '@nestjs/common';
import { RefreshTokenService } from './refresh-token.service';
import { PrismaProvider } from '@/prisma.service';

@Module({
  providers: [RefreshTokenService, PrismaProvider],
  exports: [RefreshTokenService],
})
export class RefreshTokenModule {}
