import { PrismaProvider } from '@/prisma.service';
import { Injectable } from '@nestjs/common';
import { CreateBlockFileDto } from './dtos/create-block-files.dto';
import { BlockFile, Prisma } from '@prisma/client';

@Injectable()
export class BlockFileService {
  constructor(private prisma: PrismaProvider) {}

  async blockFile(where: Prisma.BlockFileWhereUniqueInput): Promise<BlockFile | null> {
    return this.prisma.get().blockFile.findUnique({
      where: where,
    });
  }

  async blockFiles(where?: Prisma.BlockFileWhereInput): Promise<BlockFile[]> {
    return this.prisma.get().blockFile.findMany({
      where: where,
    });
  }

  async create(blockUuid: string, createBlockFileDto: CreateBlockFileDto): Promise<BlockFile> {
    // TODO: NCP Object Storage에 이미지 업로드한다.
    // - BlockFile의 UUID를 생성하고, 확장자 제외한 이름으로 NCP에 업로드한다.
    const blockFileName = `test.jpeg`;
    const { latitude, longitude } = createBlockFileDto;

    return this.prisma.get().blockFile.create({
      data: {
        blockUuid: blockUuid,
        fileName: blockFileName,
        latitude: latitude,
        longitude: longitude,
      },
    });
  }
}
