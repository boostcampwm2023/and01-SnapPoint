import { BlockDto } from '@/block/dtos/block.dto';
import { Post } from '@prisma/client';

export class PostDto {
  readonly uuid: string;

  readonly title: string;

  readonly blocks: BlockDto[];

  static of(post: Post, blockDtos: BlockDto[]) {
    return {
      uuid: post.uuid,
      title: post.title,
      blocks: blockDtos,
    };
  }
}
