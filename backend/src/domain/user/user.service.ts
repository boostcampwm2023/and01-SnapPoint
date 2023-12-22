import { BadRequestException, Injectable, NotFoundException } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import * as bcrypt from 'bcrypt';
import { Prisma, User } from '@prisma/client';
import { TxPrismaService } from '@/common/transaction/tx-prisma.service';

@Injectable()
export class UserService {
  constructor(private readonly prisma: TxPrismaService) {}

  async create(createUserDto: CreateUserDto): Promise<User> {
    const hashedPassword = await bcrypt.hash(createUserDto.password, 10);

    return this.prisma.user.create({
      data: {
        email: createUserDto.email,
        password: hashedPassword,
        nickname: createUserDto.nickname,
      },
    });
  }

  findAll(): Promise<User[]> {
    return this.prisma.user.findMany();
  }

  async findUserByUniqueInput(where: Prisma.UserWhereUniqueInput): Promise<User | null> {
    return this.prisma.user.findUnique({
      where,
    });
  }

  async findUsers(params: {
    skip?: number;
    take?: number;
    cursor?: Prisma.UserWhereUniqueInput;
    where?: Prisma.UserWhereInput;
    orderBy?: Prisma.UserOrderByWithRelationInput;
  }): Promise<User[]> {
    const { skip, take, cursor, where, orderBy } = params;
    return this.prisma.user.findMany({
      skip,
      take,
      cursor,
      where,
      orderBy,
    });
  }

  async findOne(uuid: string): Promise<User | null> {
    return this.prisma.user.findUnique({
      where: {
        uuid: uuid,
      },
    });
  }

  async update(uuid: string, updateUserDto: UpdateUserDto): Promise<User> {
    const user = await this.prisma.user.findUnique({
      where: {
        uuid: uuid,
      },
    });

    if (!user) {
      throw new BadRequestException();
    }

    const hashedPassword = await bcrypt.hash(updateUserDto.password, 10);

    return this.prisma.user.update({
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
    const user = await this.prisma.user.findUnique({
      where: {
        uuid: uuid,
      },
    });

    if (!user) {
      throw new NotFoundException('해당 유저가 존재하지 않습니다.');
    }

    return this.prisma.user.update({
      where: {
        uuid: uuid,
      },
      data: {
        isDeleted: true,
      },
    });
  }
}
