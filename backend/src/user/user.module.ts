import { Module } from '@nestjs/common';
import { UserService } from './user.service';
import { PrismaProvider } from '@/prisma/prisma.provider';

@Module({
  providers: [UserService, PrismaProvider],
  exports: [UserService],
})
export class UserModule {}
