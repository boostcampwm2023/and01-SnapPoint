import { Injectable } from '@nestjs/common';
import { CreateBlockDto } from '@/domain/block/dtos/create-block.dto';
import { Block } from '@/domain/block/entites/block.entity';
import { UpsertBlockDto } from '@/domain/block/dtos/upsert-block.dto';
import { BlockRepository } from '@/domain/block/block.repository';
import { FindBlocksByAreaDto } from '@/domain/block/dtos/find-blocks-by-area.dto';
import { FindBlocksByPostDto } from '@/domain/block/dtos/find-blocks-by-post.dto';

@Injectable()
export class BlockService {
  constructor(private readonly repository: BlockRepository) {}

  async createBlocks(postUuid: string, dtos: CreateBlockDto[]): Promise<Block[]> {
    await this.repository.createMany(postUuid, dtos);
    return this.repository.findManyByIds(dtos);
  }

  async findBlocksByArea(dto: FindBlocksByAreaDto): Promise<Block[]> {
    return this.repository.findManyByArea(dto);
  }

  async findBlocksByPost(dto: FindBlocksByPostDto): Promise<Block[]> {
    return this.repository.findManyByPosts([dto]);
  }

  async findBlocksByPosts(dtos: FindBlocksByPostDto[]): Promise<Block[]> {
    return this.repository.findManyByPosts(dtos);
  }

  async modifyBlocks(postUuid: string, dtos: UpsertBlockDto[]): Promise<Block[]> {
    await this.repository.deleteManyByPost({ postUuid });

    await Promise.all(
      dtos.map((dto) =>
        this.repository.upsertOne(postUuid, {
          ...dto,
          isDeleted: false,
        }),
      ),
    );

    return this.repository.findManyByIds(dtos);
  }

  async upsertBlock(postUuid: string, dto: UpsertBlockDto) {
    return this.repository.upsertOne(postUuid, dto);
  }

  async deleteBlocksByPost(postUuid: string) {
    const blocks = await this.repository.findManyByPost({ postUuid });
    this.repository.deleteManyByPost({ postUuid });
    return blocks;
  }
}
