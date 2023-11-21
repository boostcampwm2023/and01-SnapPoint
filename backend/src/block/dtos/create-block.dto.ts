import { CreateBlockFileDto } from '@/block-file/dtos/create-block-files.dto';
import { Type } from 'class-transformer';
import { IsString, IsIn, IsInt, IsLatitude, IsLongitude, IsOptional, ValidateNested } from 'class-validator';

export class CreateBlockDto {
  @IsString()
  readonly content: string;

  @IsInt()
  readonly order: number;

  @IsIn(['text', 'image', 'video'])
  readonly type: string;

  @IsOptional()
  @IsLatitude()
  readonly latitude?: number;

  @IsOptional()
  @IsLongitude()
  readonly longitude?: number;

  @IsOptional()
  @ValidateNested({ each: true })
  @Type(() => CreateBlockFileDto)
  readonly files?: CreateBlockFileDto[];
}
