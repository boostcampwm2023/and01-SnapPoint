import { Module } from '@nestjs/common';
import { UserService } from './user.service';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { PrismaService } from '@/prisma.service';

@Module({
  providers: [UserService, PrismaProvider, PrismaService],
  exports: [UserService],
})
export class UserModule {}
