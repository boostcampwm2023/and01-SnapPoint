import { FileDto } from '@/file/dto/file.dto';
import { Block } from '@prisma/client';

export class BlockDto {
  readonly uuid: string;

  readonly content: string;

  readonly type: string;

  readonly files?: FileDto[];

  static of(block: Block, files?: FileDto[]): BlockDto {
    return {
      uuid: block.uuid,
      content: block.content,
      type: block.type,
      files: files,
    };
  }
}
