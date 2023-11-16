import { CreateBlockFileDto } from '@/block-file/dtos/create-block-files.dto';
import { Type } from 'class-transformer';
import { IsString, IsIn, IsInt, ValidateNested, IsOptional } from 'class-validator';

export class CreateBlockDto {
  @IsString()
  readonly content: string;

  @IsInt()
  readonly order: number;

  @IsIn(['text', 'image', 'video'])
  readonly type: string;

  @IsOptional()
  @ValidateNested({ each: true })
  @Type(() => CreateBlockFileDto)
  readonly blockFiles?: CreateBlockFileDto[];
}
