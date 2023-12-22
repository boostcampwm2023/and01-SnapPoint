import { Block } from '@/domain/block/entites/block.entity';
import { FindBlocksByPostDto } from './dtos/find-blocks-by-post.dto';
import { FindBlocksByAreaDto } from './dtos/find-blocks-by-area.dto';
import { CreateBlockDto } from './dtos/create-block.dto';
import { DeleteBlocksByPostDto } from './dtos/delete-blocks-by-post.dto';
import { UpsertBlockDto } from './dtos/upsert-block.dto';
import { FindBlocksByIdDto } from './dtos/find-blocks-by-id.dto';
import { Injectable } from '@nestjs/common';
import { Prisma } from '@prisma/client';
import { Sql } from '@prisma/client/runtime/library';
import { TxPrismaService } from '@/common/transaction/tx-prisma.service';

@Injectable()
export class BlockRepository {
  constructor(private readonly prisma: TxPrismaService) {}

  async createMany(postUuid: string, dtos: CreateBlockDto[]): Promise<Block[]> {
    const values = dtos.map((dto) => {
      const { uuid, content, type, order, latitude, longitude } = dto;
      const coords = type === 'media' ? `ST_GeomFromText('POINT(${longitude} ${latitude})', 4326)` : null;
      return Prisma.sql`(${uuid}, ${postUuid}, ${content}, ${type}, ${order}, ${coords ? Prisma.raw(coords) : null})`;
    });

    return this.prisma
      .$queryRaw`INSERT INTO "Block" ("uuid", "postUuid", "content", "type", "order", "coords") VALUES ${Prisma.join(
      values,
    )}`;
  }

  async findManyByIds(dtos: FindBlocksByIdDto[]): Promise<Block[]> {
    const conditions: Sql[] = dtos.map((dto) => Prisma.sql`"uuid" = ${dto.uuid}`);

    const blocks: Block[] = await this.prisma.$queryRaw`
      SELECT    "id", "uuid", "postUuid", "type", "order", "content", 
                "createdAt", "modifiedAt", "isDeleted",
                ST_X("coords") AS "longitude", ST_Y("coords") As "latitude"
      FROM      "Block"
      WHERE     ${Prisma.join(conditions, 'OR')} AND "isDeleted" = 'false'
      ORDER BY  "order" 
    `;

    return blocks;
  }

  async findManyByPost(dto: FindBlocksByPostDto): Promise<Block[]> {
    const { postUuid } = dto;

    return this.prisma.$queryRaw`
      SELECT  "id", "uuid", "postUuid", "type", "order", "content", 
              "createdAt", "modifiedAt", "isDeleted",
              ST_X("coords") AS "longitude", ST_Y("coords") As "latitude"
      FROM    "Block"
      WHERE   "postUuid" = ${postUuid} AND "isDeleted" = 'false'
      ORDER BY  "order" 
    `;
  }

  async findManyByPosts(dtos: FindBlocksByPostDto[]): Promise<Block[]> {
    const conditions: Sql[] = dtos.map((dto) => Prisma.sql`"postUuid" = ${dto.postUuid}`);

    return this.prisma.$queryRaw`
      SELECT    "id", "uuid", "postUuid", "type", "order", "content", 
                "createdAt", "modifiedAt", "isDeleted",
                ST_X("coords") AS "longitude", ST_Y("coords") As "latitude"
      FROM      "Block"
      WHERE     ${Prisma.join(conditions, 'OR')} AND "isDeleted" = 'false'
      ORDER BY  "order" 
    `;
  }

  async findManyByArea(dto: FindBlocksByAreaDto): Promise<Block[]> {
    const { latitudeMin: latMin, longitudeMin: lonMin, latitudeMax: latMax, longitudeMax: lonMax } = dto;

    // TODO: 여유를 줄 수 있는 방법을 찾아본다.
    return this.prisma.$queryRaw`
      SELECT    "id", "uuid", "postUuid", "type", "order", "content", 
                "createdAt", "modifiedAt", "isDeleted",
                ST_X("coords") AS "longitude", ST_Y("coords") As "latitude"
      FROM      "Block"
      WHERE     "isDeleted" = 'false' and "type" = 'media' AND "coords" IS NOT NULL
                AND ST_Intersects(coords, ST_MakeEnvelope(${lonMin}, ${latMin}, ${lonMax}, ${latMax}, 4326))
      ORDER BY  ST_Distance(coords, ST_Centroid(ST_MakeEnvelope(${lonMin}, ${latMin}, ${lonMax}, ${latMax}, 4326)));
    `;
  }

  async upsertOne(postUuid: string, dto: UpsertBlockDto) {
    const { uuid, content, type, order, latitude, longitude, isDeleted } = dto;

    const coords = type === 'media' ? `ST_GeomFromText('POINT(${longitude} ${latitude})', 4326)` : null;

    const query = Prisma.sql`
      INSERT INTO "Block" ("uuid", "postUuid", "content", "type", "order", "coords")
      VALUES (${uuid}, ${postUuid}, ${content}, ${type}, ${order}, ${coords ? Prisma.raw(coords) : null})
      ON CONFLICT ("uuid")
      DO UPDATE SET
        "content" = ${content},
        "order" = ${order},
        "coords" = ${coords ? Prisma.raw(coords) : null},
        "isDeleted" = ${isDeleted}
    `;

    return this.prisma.$queryRaw(query);
  }

  async deleteManyByPost(dto: DeleteBlocksByPostDto): Promise<number> {
    const { postUuid } = dto;

    const { count } = await this.prisma.block.updateMany({
      data: { isDeleted: true },
      where: { postUuid },
    });
    return count;
  }
}
