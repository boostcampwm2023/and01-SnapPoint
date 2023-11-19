import { Injectable } from '@nestjs/common';
import { Prisma, Block } from '@prisma/client';
import { PrismaProvider } from '@/prisma.service';
import { CreateBlockDto } from './dtos/create-block.dto';

@Injectable()
export class BlockService {
  constructor(private prisma: PrismaProvider) {}

  async create(postUuid: string, createBlockDto: CreateBlockDto): Promise<Block> {
    const { order, type, content } = createBlockDto;

    return this.prisma.get().block.create({
      data: {
        postUuid: postUuid,
        order: order,
        type: type,
        content: content,
      },
    });
  }

  async block(where: Prisma.BlockWhereUniqueInput): Promise<Block | null> {
    return this.prisma.get().block.findUnique({
      where,
    });
  }

  async blocks(where?: Prisma.BlockWhereInput): Promise<Block[] | null> {
    return this.prisma.get().block.findMany({
      where,
    });
  }
}
