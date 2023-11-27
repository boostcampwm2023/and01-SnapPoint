import { BadRequestException, Injectable, NotFoundException } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { PrismaProvider } from '@/prisma/prisma.provider';
import * as bcrypt from 'bcrypt';
import { Prisma, User } from '@prisma/client';

@Injectable()
export class UserService {
  constructor(private prisma: PrismaProvider) {}

  async create(createUserDto: CreateUserDto): Promise<User> {
    const hashedPassword = await bcrypt.hash(createUserDto.password, 10);

    return this.prisma.get().user.create({
      data: {
        email: createUserDto.email,
        password: hashedPassword,
        nickname: createUserDto.nickname,
      },
    });
  }

  findAll(): Promise<User[]> {
    return this.prisma.get().user.findMany();
  }

  async findUserByUniqueInput(where: Prisma.UserWhereUniqueInput): Promise<User | null> {
    return this.prisma.get().user.findUnique({
      where,
    });
  }

  async findOne(uuid: string): Promise<User | null> {
    return this.prisma.get().user.findUnique({
      where: {
        uuid: uuid,
      },
    });
  }

  async update(uuid: string, updateUserDto: UpdateUserDto): Promise<User> {
    const user = await this.prisma.get().user.findUnique({
      where: {
        uuid: uuid,
      },
    });

    if (!user) {
      throw new BadRequestException();
    }

    const hashedPassword = await bcrypt.hash(updateUserDto.password, 10);

    return this.prisma.get().user.update({
      where: {
        uuid: uuid,
      },
      data: {
        password: hashedPassword,
        nickname: updateUserDto.nickname,
      },
    });
  }

  async remove(uuid: string) {
    const user = await this.prisma.get().user.findUnique({
      where: {
        uuid: uuid,
      },
    });

    if (!user) {
      throw new NotFoundException('해당 유저가 존재하지 않습니다.');
    }

    return this.prisma.get().user.update({
      where: {
        uuid: uuid,
      },
      data: {
        isDeleted: true,
      },
    });
  }
}
