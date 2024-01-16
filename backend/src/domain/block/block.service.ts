import { Injectable } from '@nestjs/common';
import { CreateBlockDto } from '@/domain/block/dtos/create-block.dto';
import { UpsertBlockDto } from '@/domain/block/dtos/upsert-block.dto';
import { BlockRepository } from '@/domain/block/block.repository';
import { FindBlocksByAreaDto } from '@/domain/block/dtos/find-blocks-by-area.dto';
import { FindBlocksByPostDto } from '@/domain/block/dtos/find-blocks-by-post.dto';
import { Block } from '@prisma/client';
import { FindBlocksDto } from './dtos/find-blocks.dto';

@Injectable()
export class BlockService {
  constructor(private readonly repository: BlockRepository) {}

  async createBlocks(postUuid: string, dtos: CreateBlockDto[]): Promise<Block[]> {
    const data = dtos.map((dto) => ({ ...dto, postUuid }));
    return this.repository.createMany({ data });
  }

  async findBlocksByArea(dto: FindBlocksByAreaDto): Promise<Block[]> {
    return this.repository.findManyByArea(dto);
  }

  async findBlocksByIdList(dto: FindBlocksDto[]): Promise<Block[]> {
    return this.repository.findMany({ where: { OR: dto } });
  }

  async findBlocksByPost(dto: FindBlocksByPostDto): Promise<Block[]> {
    const { postUuid } = dto;

    return this.repository.findMany({
      where: { postUuid },
    });
  }

  async findBlocksByPosts(dtos: FindBlocksByPostDto[]): Promise<Block[]> {
    return this.repository.findMany({
      where: { AND: [{ OR: dtos }] },
    });
  }

  async modifyBlocks(postUuid: string, dtos: UpsertBlockDto[]): Promise<Block[]> {
    await this.repository.deleteMany({ where: { postUuid } });

    const data = dtos.map((dto) => ({ ...dto, postUuid }));
    return this.repository.upsertMany({ data });
  }

  async deleteBlocksByPost(postUuid: string) {
    // TODO: DeletePost App 서비스 리팩토링 후, Delete는 Count만 반환하게 변경한다.
    const blocks = await this.repository.findMany({ where: { postUuid } });
    await this.repository.deleteMany({ where: { postUuid } });
    return blocks;
  }
}
