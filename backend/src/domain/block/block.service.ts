import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { Injectable } from '@nestjs/common';
import { Prisma } from '@prisma/client';
import { CreateBlockDto } from '@/domain/block/dtos/create-block.dto';
import { Block } from './entites/block.entity';
import { UpsertBlockDto } from './dtos/upsert-block.dto';

@Injectable()
export class BlockService {
  constructor(private readonly prisma: PrismaProvider) {}

  async createBlock(postUuid: string, dto: CreateBlockDto) {
    const { uuid, content, type, order, latitude, longitude } = dto;

    const coords = type === 'media' ? `ST_GeomFromText(POINT(${longitude} ${latitude}), 4326))` : null;
    return this.prisma.get()
      .$queryRaw`INSERT INTO "Block" ("uuid", "postUuid", "content", "type", "order", "coords") VALUES (${uuid}, ${postUuid}, ${content}, ${type}, ${order}, ${coords}`;
  }

  async createBlocks(postUuid: string, dtos: CreateBlockDto[]) {
    // PreparedStatement를 사용하도록 변경한다.
    const values = dtos
      .map((dto) => {
        const { uuid, content, type, order, latitude, longitude } = dto;
        const coords = type === 'media' ? `ST_GeomFromText('POINT(${longitude} ${latitude})', 4326)` : null;
        return `('${uuid}', '${postUuid}', '${content}', '${type}', ${order}, ${coords})`;
      })
      .join(', ');

    const query = 'INSERT INTO "Block" ("uuid", "postUuid", "content", "type", "order", "coords") VALUES ' + values;
    return this.prisma.get().$queryRawUnsafe(query);
  }

  async findBlock(blockWhereUniqueInput: Prisma.BlockWhereUniqueInput): Promise<Block | null> {
    const block = await this.prisma.get().block.findUnique({
      where: { ...blockWhereUniqueInput, isDeleted: false },
    });
    return block;
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

  async findBlocksWithCoordsByPost(postUuid: string): Promise<Block[]> {
    const blocks: Block[] = await this.prisma.get().$queryRaw`
      SELECT  "id", "uuid", "postUuid", "type", "order", "content", 
              "createdAt", "modifiedAt", "isDeleted",
              ST_X("coords") AS "latitude", ST_Y("coords") As "longitude"
      FROM    "Block"
      WHERE   "postUuid" = ${postUuid}
    `;
    return blocks;
  }

  async updateBlock(params: { where: Prisma.BlockWhereUniqueInput; data: CreateBlockDto }) {
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

  async deleteBlocks(where: Prisma.BlockWhereInput) {
    return this.prisma.get().block.updateMany({
      data: { isDeleted: true },
      where,
    });
  }

  async upsertBlock(postUuid: string, dto: UpsertBlockDto) {
    const { uuid, content, type, order, latitude, longitude } = dto;
    const wktString = `POINT(${longitude} ${latitude})`;

    return this.prisma.get().$queryRaw`
        INSERT INTO "Block" ("uuid", "postUuid", "content", "type", "order", "coords")
        VALUES (${uuid}, ${postUuid}, ${content}, ${type}, ${order}, ST_GeomFromText(${wktString}, 4326))
        ON CONFLICT (uuid)
        DO UPDATE SET
          "postUuid" = ${postUuid},
          "content" = ${content},
          "type" = ${type},
          "order" = ${order},
          "coords" = ST_GeomFromText(${wktString}, 4326)
      `;
  }
}
