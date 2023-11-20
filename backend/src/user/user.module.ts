import { Module } from '@nestjs/common';
import { UserService } from './user.service';
import { UserController } from './user.controller';
import { PrismaProvider } from '@/prisma.service';

@Module({
  controllers: [UserController],
  providers: [UserService, PrismaProvider],
})
export class UserModule {}
