import { DecomposedBlockDto } from './decomposed-block.dto';
import { DecomposedFileDto } from './decomposed-file.dto';
import { DecomposedPostDto } from './decomposed-post.dto';

export class DecomposedPostDataDto {
  post: DecomposedPostDto;

  blocks: DecomposedBlockDto[];

  files: DecomposedFileDto[];
}
