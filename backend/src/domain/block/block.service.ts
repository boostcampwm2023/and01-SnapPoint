import { Injectable } from '@nestjs/common';
import { CreateBlockDto } from '@/domain/block/dtos/create-block.dto';
import { Block } from '@/domain/block/entites/block.entity';
import { UpsertBlockDto } from '@/domain/block/dtos/upsert-block.dto';
import { BlockRepository } from '@/domain/block/block.repository';
import { FindBlocksByAreaDto } from '@/domain/block/dtos/find-blocks-by-area.dto';
import { FindBlocksByPostDto } from '@/domain/block/dtos/find-blocks-by-post.dto';
import { FindBlocksByIdDto } from '@/domain/block/dtos/find-blocks-by-id.dto';

@Injectable()
export class BlockService {
  constructor(private readonly repository: BlockRepository) {}

  async createBlocks(postUuid: string, dtos: CreateBlockDto[]) {
    return this.repository.createMany(postUuid, dtos);
  }

  async findBlocksById(dto: FindBlocksByIdDto): Promise<Block[]> {
    return this.repository.findManyById(dto);
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

  async modifyBlocks(postUuid: string, dtos: UpsertBlockDto[]) {
    await this.repository.deleteManyByPost({ postUuid });

    return await Promise.all(
      dtos.map((dto) =>
        this.repository.upsertOne(postUuid, {
          ...dto,
          isDeleted: false,
        }),
      ),
    );
  }

  async upsertBlock(postUuid: string, dto: UpsertBlockDto) {
    return this.repository.upsertOne(postUuid, dto);
  }

  getSummaryContent(blocks: Block[]): string | null {
    // 문장 추출
    // 2,000자 이상인 경우 문장 단위로 2,000자 이하로 맞춘다.
    const content = blocks
      .map((blocks) => blocks.content)
      .join('\n')
      .substring(0, 1900);

    const hasSentenceValidLength = (sentence: string): boolean => sentence.length <= 200;
    const hasEnoughWords = (sentence: string): boolean => sentence.trim().split(/\s+/).length >= 5;
    const isValidSentence = (sentence: string): boolean => hasSentenceValidLength(sentence) && hasEnoughWords(sentence);

    // 검사를 위해 문장으로 분리한다.
    const sentences = content.split(/[.?!]\s/).filter((sentence) => sentence.trim() !== '');

    const isValidContent = sentences.every(isValidSentence) && sentences.length > 0;

    return isValidContent ? content : null;
  }

  getBlockContent(blocks: Block[]): string {
    for (const block of blocks) {
      if (block.type === 'text') return block.content;
    }
    return blocks[0].content;
  }

  filterMediaBlocks(blocks: Block[]) {
    return blocks.filter((block) => block.type === 'media');
  }
}
