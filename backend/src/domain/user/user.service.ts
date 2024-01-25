import { Inject, Injectable } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import * as bcrypt from 'bcrypt';
import { Prisma, User } from '@prisma/client';
import { PRISMA_SERVICE, PrismaService } from '@/common/databases/prisma.service';
import { FindUserByIdDto } from './dto/find-user-by-id.dto';
import { findUserByEmailDto } from './dto/find-user-by-email.dto';
import { DeleteUserDto } from './dto/delete-user.dto';
import { VerifyPasswordDto } from './dto/verify-password.dto';

@Injectable()
export class UserService {
  constructor(@Inject(PRISMA_SERVICE) private readonly prisma: PrismaService) {}

  async createUser(createUserDto: CreateUserDto): Promise<User> {
    const { email, password, nickname } = createUserDto;

    const hashedPassword = await this.hashPassword(password);

    return this.prisma.user.create({
      data: { email, nickname, password: hashedPassword },
    });
  }

  async findUserByEmail(findUserByEmailDto: findUserByEmailDto) {
    const { email } = findUserByEmailDto;

    return this.prisma.user.findUnique({
      where: { email, isDeleted: false },
    });
  }

  async findUserById(findUserByIdDto: FindUserByIdDto) {
    const { uuid } = findUserByIdDto;

    return this.prisma.user.findUnique({
      where: { uuid, isDeleted: false },
    });
  }

  async findUsersByIds(findUserByIdDtos: FindUserByIdDto[]) {
    const conditions = findUserByIdDtos.map(({ uuid }) => uuid);

    return this.prisma.user.findMany({
      where: { uuid: { in: conditions }, isDeleted: false },
    });
  }

  async updateUser(uuid: string, updateUserDto: UpdateUserDto): Promise<User> {
    const { nickname, password } = updateUserDto;

    const updateData: Prisma.UserUpdateInput = { nickname };

    if (password) {
      updateData.password = await this.hashPassword(password);
    }

    return this.prisma.user.update({
      where: { uuid },
      data: updateData,
    });
  }

  async deleteUser(deleteUserDto: DeleteUserDto) {
    const { uuid } = deleteUserDto;

    return this.prisma.user.update({
      where: { uuid },
      data: { isDeleted: true },
    });
  }

  async verifyPassword(verifyPasswordDto: VerifyPasswordDto) {
    const { password, hashedPassword } = verifyPasswordDto;

    return bcrypt.compare(password, hashedPassword);
  }

  private async hashPassword(password: string): Promise<string> {
    return bcrypt.hash(password, 10);
  }
}
