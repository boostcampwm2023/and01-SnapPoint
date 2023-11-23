import { PrismaProvider } from '@/prisma/prisma.provider';
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

  async save(blockUuid: string, createBlockFileDto: CreateBlockFileDto) {
    const { uuid: fileUuid } = createBlockFileDto;

    const blockFile = await this.prisma.get().blockFile.findFirst({ where: { blockUuid, fileUuid } });

    if (blockFile) return blockFile;

    return this.prisma.get().blockFile.create({
      data: {
        fileUuid: createBlockFileDto.uuid,
        blockUuid: blockUuid,
      },
    });
  }
}
