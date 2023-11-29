import { Module } from '@nestjs/common';
import { UserService } from './user.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PrismaService } from '@/common/prisma/prisma.service';

@Module({
  providers: [UserService, PrismaProvider, PrismaService],
  exports: [UserService],
})
export class UserModule {}
