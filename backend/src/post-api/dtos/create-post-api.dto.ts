import { CreateBlockDto } from '@/block/dtos/create-block.dto';
import { Type } from 'class-transformer';
import { ArrayNotEmpty, IsString, ValidateNested } from 'class-validator';

export class CreatePostApiDto {
  @IsString()
  readonly userEmail: string;

  @IsString()
  readonly title: string;

  @ArrayNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => CreateBlockDto)
  readonly blocks: CreateBlockDto[];
}
