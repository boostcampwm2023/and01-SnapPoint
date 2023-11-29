import { BlockDto } from '@/domain/block/dtos/block.dto';
import { ApiProperty } from '@nestjs/swagger';
import { Post } from '@prisma/client';

export class PostDto {
  @ApiProperty({ description: '게시글을 나타내는 고유한 식별자입니다.' })
  readonly uuid: string;

  @ApiProperty({ description: '게시글의 제목입니다.' })
  readonly title: string;

  @ApiProperty({ description: '게시글의 생성 일시입니다.' })
  readonly createdAt: string;

  @ApiProperty({ description: '게시글의 수정 일시입니다.' })
  readonly modifiedAt: string;

  @ApiProperty({ type: BlockDto, isArray: true })
  readonly blocks: BlockDto[];

  static of(post: Post, blockDtos: BlockDto[]) {
    return {
      uuid: post.uuid,
      title: post.title,
      createdAt: post.createdAt,
      modifiedAt: post.modifiedAt,
      blocks: blockDtos,
    };
  }
}
