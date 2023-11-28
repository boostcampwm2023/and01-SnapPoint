import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { Injectable } from '@nestjs/common';
import { Block, Prisma } from '@prisma/client';
import { CreateBlockDto } from '@/domain/block/dtos/create-block.dto';

@Injectable()
export class BlockService {
  constructor(private readonly prisma: PrismaProvider) {}

  async createBlock(postUuid: string, dto: CreateBlockDto) {
    const { uuid, content, type, order, latitude, longitude } = dto;
    return this.prisma.get().block.create({ data: { uuid, content, type, order, latitude, longitude, postUuid } });
  }

  async createBlocks(postUuid: string, dtos: CreateBlockDto[]) {
    const data = dtos.map((dto) => {
      const { uuid, content, type, order, latitude, longitude } = dto;
      return { uuid, content, type, order, latitude, longitude, postUuid };
    });
    return this.prisma.get().block.createMany({ data });
  }

  async findBlock(blockWhereUniqueInput: Prisma.BlockWhereUniqueInput): Promise<Block | null> {
    return this.prisma.get().block.findUnique({
      where: { ...blockWhereUniqueInput, isDeleted: false },
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
      where: { ...where, isDeleted: false },
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
