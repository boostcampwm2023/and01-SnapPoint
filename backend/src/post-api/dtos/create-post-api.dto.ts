import { CreateBlockDto } from '@/block/dtos/create-block.dto';
import { Type } from 'class-transformer';
import { ArrayNotEmpty, IsOptional, IsString, ValidateNested } from 'class-validator';

export class CreatePostApiDto {
  @IsOptional()
  @IsString()
  readonly title?: string;

  @ArrayNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => CreateBlockDto)
  readonly blocks: CreateBlockDto[];
}
