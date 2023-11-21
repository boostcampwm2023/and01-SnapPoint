import { PrismaProvider } from '@/prisma.service';
import { Injectable } from '@nestjs/common';
import { CreateBlockFileDto } from './dtos/create-block-files.dto';
import { BlockFile, Prisma } from '@prisma/client';

@Injectable()
export class BlockFileService {
  constructor(private prisma: PrismaProvider) {}

  async findMany(where?: Prisma.BlockFileWhereInput): Promise<BlockFile[]> {
    return this.prisma.get().blockFile.findMany({
      where: where,
    });
  }

  async attachFile(blockUuid: string, createBlockFileDto: CreateBlockFileDto) {
    return this.prisma.get().blockFile.create({
      data: {
        fileUuid: createBlockFileDto.uuid,
        blockUuid: blockUuid,
      },
    });
  }
}
