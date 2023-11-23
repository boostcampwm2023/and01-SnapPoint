import { PrismaProvider } from '@/prisma/prisma.provider';
import { Injectable } from '@nestjs/common';
import { CreateFileDto } from './dto/create-file.dto';
import { UpdateFileDto } from './dto/update-file.dto';
import { BucketService } from '@/bucket.service';
import { File, Prisma } from '@prisma/client';
import { randomUUID } from 'crypto';

@Injectable()
export class FileService {
  constructor(
    private readonly bucketService: BucketService,
    private readonly prismaProvider: PrismaProvider,
  ) {}

  async create(createFileDto: CreateFileDto) {
    const { file } = createFileDto;

    return this.prismaProvider.beginTransaction(async () => {
      file.filename = randomUUID();
      const { Location: url, Key: fileUuid } = await this.bucketService.uploadFile(file);

      const createdFile = await this.prismaProvider.get().file.create({
        data: {
          uuid: fileUuid,
          userUuid: 'test',
          mimeType: file.mimetype,
          url: url,
        },
      });
      return this.findOne(createdFile.uuid);
    });
  }

  async findOne(uuid: string): Promise<File> {
    const file = await this.prismaProvider.get().file.findUnique({ where: { uuid } });
    return file;
  }

  async findMany(where?: Prisma.FileWhereInput): Promise<File[]> {
    return this.prismaProvider.get().file.findMany({
      where: where,
    });
  }
  update(id: number, updateFileDto: UpdateFileDto) {
    updateFileDto;
    return `This action updates a #${id} file`;
  }

  remove(id: number) {
    return `This action removes a #${id} file`;
  }
}
