import { UpdateFileDto } from '@/domain/file/dtos/update-file.dto';
import { CreateBlockDto } from '@/domain/block/dtos/create-block.dto';
import { CreatePostDto } from '@/domain/post/dtos/create-post.dto';

export class DecomposedPostDto {
  post: CreatePostDto;

  blocks: CreateBlockDto[];

  files: UpdateFileDto[];
}
