import { ArrayNotEmpty, IsOptional, IsString, ValidateNested } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';
import { Type } from 'class-transformer';
import { WriteBlockDto } from './write-block.dto';

export class WritePostDto {
  @ApiProperty({ description: '게시글의 제목을 나타냅니다.' })
  @IsOptional()
  @IsString()
  readonly title: string;

  @ArrayNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => WriteBlockDto)
  blocks: WriteBlockDto[];
}
