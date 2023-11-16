import { Injectable } from '@nestjs/common';
import { Prisma, Block } from '@prisma/client';
import { PrismaService } from '@/prisma.service';
import { CreateBlockDto } from './dtos/create-block.dto';

@Injectable()
export class BlockService {
  constructor(private prisma: PrismaService) {}

  async create(postUuid: string, createBlockDto: CreateBlockDto): Promise<Block> {
    const { order, type, content } = createBlockDto;

    return this.prisma.block.create({
      data: {
        postUuid: postUuid,
        order: order,
        type: type,
        content: content,
      },
    });
  }

  async block(where: Prisma.BlockWhereUniqueInput): Promise<Block | null> {
    return this.prisma.block.findUnique({
      where,
    });
  }

  async blocks(where?: Prisma.BlockWhereInput): Promise<Block[] | null> {
    return this.prisma.block.findMany({
      where,
    });
  }
}
