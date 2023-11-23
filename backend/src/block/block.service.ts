import { BadRequestException, Injectable, NotFoundException } from '@nestjs/common';
import { Prisma, Block } from '@prisma/client';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { CreateBlockDto } from './dtos/create-block.dto';

@Injectable()
export class BlockService {
  constructor(private readonly prisma: PrismaProvider) {}

  async create(postUuid: string, createBlockDto: CreateBlockDto) {
    const { order, type, content, latitude, longitude } = createBlockDto;

    if (type === 'text' && (latitude || longitude)) {
      throw new BadRequestException('Latitude and longitude should not be provided for media type');
    }

    if (type === 'media' && (!latitude || !longitude)) {
      throw new BadRequestException('Latitude and longitude should be provided for media type');
    }

    return this.prisma.get().block.create({
      data: {
        postUuid: postUuid,
        order: order,
        type: type,
        content: content,
        latitude: latitude,
        longitude: longitude,
      },
    });
  }

  async save(postUuid: string, blockDto: CreateBlockDto) {
    if (!blockDto.uuid) {
      return this.create(postUuid, blockDto);
    }

    const block = await this.findOne(blockDto.uuid);

    if (!block) {
      throw new NotFoundException(`Cloud not found block with UUID: ${blockDto.uuid}`);
    }

    if (block.postUuid !== postUuid) {
      throw new BadRequestException(`The block is not part of Post[UUID: ${postUuid}]`);
    }

    return this.prisma.get().block.update({
      data: blockDto,
      where: { uuid: blockDto.uuid },
    });
  }

  async findOne(uuid: string): Promise<Block | null> {
    const block = await this.prisma.get().block.findUnique({ where: { uuid: uuid } });
    return block;
  }

  async findAll(): Promise<Block[]> {
    return this.prisma.get().block.findMany();
  }

  async findMany(where?: Prisma.BlockWhereInput): Promise<Block[]> {
    return this.prisma.get().block.findMany({
      where,
    });
  }

  async update(uuid: string, updateDto: Prisma.BlockUpdateInput) {
    return this.prisma.get().block.update({
      data: updateDto,
      where: { uuid },
    });
  }

  async getOrdered(postUuid: string): Promise<Block[]> {
    return this.prisma.get().block.findMany({
      where: { postUuid },
      orderBy: { order: 'asc' },
    });
  }
}
