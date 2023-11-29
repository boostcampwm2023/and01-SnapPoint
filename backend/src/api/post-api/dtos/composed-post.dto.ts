import { WritePostDto } from './write-post.dto';
import { WriteBlockDto } from './write-block.dto';
import { Type } from 'class-transformer';
import { ArrayNotEmpty, ValidateNested } from 'class-validator';
import { WriteBlockFileDto } from './write-block-files.dto';

export class ComposedPostDto {
  @ValidateNested()
  @Type(() => WritePostDto)
  post: WritePostDto;

  @ArrayNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => WriteBlockDto)
  blocks: WriteBlockDto[];

  @ValidateNested({ each: true })
  @Type(() => WriteBlockFileDto)
  files: WriteBlockFileDto[];
}
