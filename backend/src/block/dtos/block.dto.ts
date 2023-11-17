import { BlockFileDto } from '@/block-file/dtos/block-files.dto';
import { Type } from 'class-transformer';
import { IsString, IsIn, IsInt, ValidateNested, IsOptional } from 'class-validator';
import { Block } from '@prisma/client';

export class BlockDto {
  @IsString()
  readonly content: string;

  @IsInt()
  readonly order: number;

  @IsIn(['text', 'image', 'video'])
  readonly type: string;

  @IsOptional()
  @ValidateNested({ each: true })
  @Type(() => BlockFileDto)
  readonly blockFiles?: BlockFileDto[];

  static of(block: Block, blockFileDtos: BlockFileDto[]): BlockDto {
    return {
      content: block.content,
      order: block.order,
      type: block.type,
      blockFiles: blockFileDtos,
    };
  }
}
