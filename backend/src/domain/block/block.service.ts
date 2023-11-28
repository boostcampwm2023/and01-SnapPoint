import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { Injectable } from '@nestjs/common';
import { Block, Prisma } from '@prisma/client';

@Injectable()
export class BlockService {
  constructor(private readonly prisma: PrismaProvider) {}

  async createBlock(data: Prisma.BlockCreateInput) {
    return this.prisma.get().block.create({ data });
  }

  async findBlock(blockWhereUniqueInput: Prisma.BlockWhereUniqueInput): Promise<Block | null> {
    return this.prisma.get().block.findUnique({
      where: blockWhereUniqueInput,
    });
  }

  async findBlocks(params: {
    skip?: number;
    take?: number;
    cursor?: Prisma.BlockWhereUniqueInput;
    where?: Prisma.BlockWhereInput;
    orderBy?: Prisma.BlockOrderByWithRelationInput;
  }): Promise<Block[]> {
    const { skip, take, cursor, where, orderBy } = params;
    return this.prisma.get().block.findMany({
      skip,
      take,
      cursor,
      where,
      orderBy,
    });
  }

  async updateBlock(params: { where: Prisma.BlockWhereUniqueInput; data: Prisma.BlockUpdateInput }) {
    const { data, where } = params;
    return this.prisma.get().block.update({
      data,
      where,
    });
  }

  async deleteBlock(where: Prisma.BlockWhereUniqueInput): Promise<Block> {
    return this.prisma.get().block.update({
      data: { isDeleted: true },
      where,
    });
  }
}
