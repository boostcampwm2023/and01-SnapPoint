import { FindBlocksByAreaDto } from './dtos/find-blocks-by-area.dto';
import { Inject, Injectable } from '@nestjs/common';
import { Block, Prisma } from '@prisma/client';
import { PRISMA_SERVICE, PrismaService } from '@/common/databases/prisma.service';

@Injectable()
export class BlockRepository {
  constructor(@Inject(PRISMA_SERVICE) private readonly prisma: PrismaService) {}

  async createMany(params: { data: Prisma.BlockCreateInput[] }): Promise<Block[]> {
    const { data } = params;
    const createPromises = data.map((block) => this.prisma.block.create({ data: block }));
    return Promise.all(createPromises);
  }

  async findMany(params: {
    cursor?: Prisma.BlockWhereUniqueInput;
    where?: Prisma.BlockWhereInput;
    orderBy?: Prisma.BlockOrderByWithRelationInput;
  }) {
    const { cursor, where, orderBy } = params;

    return this.prisma.block.findMany({ cursor, where: { ...where, isDeleted: false }, orderBy });
  }

  async findManyByArea(dto: FindBlocksByAreaDto): Promise<Block[]> {
    const { latitudeMin: latMin, longitudeMin: lonMin, latitudeMax: latMax, longitudeMax: lonMax } = dto;

    // isDeleted 추가
    return this.prisma.$queryRaw`
      SELECT *,
            SQRT(POW(latitude - ((${latMin} + ${latMax}) / 2), 2) + POW(longitude - ((${lonMin} + ${lonMax}) / 2), 2)) as Distance
      FROM block
      WHERE (latitude BETWEEN ${latMin} and ${latMax}) and (longitude BETWEEN ${lonMin} and ${lonMax})
      ORDER BY Distance
      LIMIT 30
  `;
  }

  async upsertMany(params: { data: Prisma.BlockCreateInput[] }): Promise<Block[]> {
    const { data } = params;

    const upsertPromises = data.map((block) =>
      this.prisma.block.upsert({
        where: { uuid: block.uuid, isDeleted: false },
        update: block,
        create: block,
      }),
    );

    return Promise.all(upsertPromises);
  }

  async deleteMany(params: { where: Prisma.BlockWhereInput }): Promise<number> {
    const { where } = params;

    const { count } = await this.prisma.block.updateMany({
      data: { isDeleted: true },
      where: where,
    });
    return count;
  }
}
