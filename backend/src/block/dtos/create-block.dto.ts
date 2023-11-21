import { CreateBlockFileDto } from '@/block-file/dtos/create-block-files.dto';
import { Type } from 'class-transformer';
import { IsString, IsIn, IsLatitude, IsLongitude, IsOptional, ValidateNested } from 'class-validator';

export class CreateBlockDto {
  @IsString()
  readonly content: string;

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

  order: number;
}
