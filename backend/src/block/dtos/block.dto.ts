import { BlockFileDto } from '@/block-file/dtos/block-files.dto';
import { Block } from '@prisma/client';

export class BlockDto {
  readonly uuid: string;

  readonly content: string;

  readonly order: number;

  readonly type: string;

  readonly blockFiles?: BlockFileDto[];

  static of(block: Block, blockFileDtos: BlockFileDto[]): BlockDto {
    return {
      uuid: block.uuid,
      content: block.content,
      order: block.order,
      type: block.type,
      blockFiles: blockFileDtos,
    };
  }
}
