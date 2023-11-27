import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { Injectable } from '@nestjs/common';
import { File, Prisma } from '@prisma/client';

@Injectable()
export class FileService {
  constructor(private readonly prisma: PrismaProvider) {}

  async createFile(data: Prisma.FileCreateInput) {
    return this.prisma.get().file.create({ data });
  }

  async findFile(fileWhereUniqueInput: Prisma.FileWhereUniqueInput): Promise<File | null> {
    return this.prisma.get().file.findUnique({
      where: fileWhereUniqueInput,
    });
  }

  async findFiles(params: {
    skip?: number;
    take?: number;
    cursor?: Prisma.FileWhereUniqueInput;
    where?: Prisma.FileWhereInput;
    orderBy?: Prisma.FileOrderByWithRelationInput;
  }): Promise<File[]> {
    const { skip, take, cursor, where, orderBy } = params;
    return this.prisma.get().file.findMany({
      skip,
      take,
      cursor,
      where,
      orderBy,
    });
  }

  async updateFile(params: { where: Prisma.FileWhereUniqueInput; data: Prisma.FileUpdateInput }) {
    const { data, where } = params;
    return this.prisma.get().file.update({
      data,
      where,
    });
  }

  async deleteFile(where: Prisma.FileWhereUniqueInput): Promise<File> {
    return this.prisma.get().file.update({
      data: { isDeleted: true },
      where,
    });
  }
}
