import { SaveBlockDto } from '@/block/dtos/save-block.dto';
import { Type } from 'class-transformer';
import { ArrayNotEmpty, IsString, ValidateNested } from 'class-validator';

export class SavePostApiDto {
  @IsString()
  readonly title: string;

  @ArrayNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => SaveBlockDto)
  readonly blocks: SaveBlockDto[];
}
