import { BadRequestException, Injectable, InternalServerErrorException } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { PrismaProvider } from '@/prisma.service';
import * as bcrypt from 'bcrypt';
import { Prisma, User } from '@prisma/client';

@Injectable()
export class UserService {
  constructor(private prisma: PrismaProvider) {}

  async create(createUserDto: CreateUserDto): Promise<User> {
    const hashedPassword = await bcrypt.hash(createUserDto.password, 10);

    const createdUser = await this.prisma.get().user.create({
      data: {
        email: createUserDto.email,
        password: hashedPassword,
        nickname: createUserDto.nickname,
      },
    });

    if (!createdUser) {
      throw new InternalServerErrorException();
    }

    return createdUser;
  }

  findAll(): Promise<User[]> {
    return this.prisma.get().user.findMany();
  }

  async findUserByUniqueInput(where: Prisma.UserWhereUniqueInput): Promise<User | null> {
    const user = await this.prisma.get().user.findUnique({
      where,
    });

    return user;
  }

  async findOne(uuid: string): Promise<User> {
    const user = await this.prisma.get().user.findUnique({
      where: {
        uuid: uuid,
      },
    });

    if (!user) {
      throw new BadRequestException();
    }

    return user;
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

    const updatedUser = await this.prisma.get().user.update({
      where: {
        uuid: uuid,
      },
      data: {
        password: hashedPassword,
        nickname: updateUserDto.nickname,
      },
    });

    return updatedUser;
  }

  async remove(uuid: string) {
    const user = await this.prisma.get().user.findUnique({
      where: {
        uuid: uuid,
      },
    });

    if (!user) {
      throw new BadRequestException();
    }

    const deletedUser = await this.prisma.get().user.update({
      where: {
        uuid: uuid,
      },
      data: {
        isDeleted: true,
      },
    });

    return deletedUser;
  }
}
