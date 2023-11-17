import { BlockDto } from '@/block/dtos/block.dto';
import { Post } from '@prisma/client';
import { Type } from 'class-transformer';
import { ArrayNotEmpty, IsString, ValidateNested } from 'class-validator';

export class PostDto {
  @IsString()
  readonly title: string;

  @ArrayNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => BlockDto)
  readonly blocks: BlockDto[];

  static of(post: Post, blockDtos: BlockDto[]) {
    return {
      title: post.title,
      blocks: blockDtos,
    };
  }
}
