import { IsOptional, IsString } from 'class-validator';
import { CreateBlockDto } from './create-block.dto';

export class SaveBlockDto extends CreateBlockDto {
  @IsOptional()
  @IsString()
  readonly uuid?: string;
}
