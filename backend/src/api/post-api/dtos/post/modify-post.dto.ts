import { ArrayNotEmpty, IsOptional, IsString, ValidateNested } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';
import { Type } from 'class-transformer';
import { ModifyBlockDto } from '../block/modify-block.dto';

export class ModifyPostDto {
  @ApiProperty({ description: '변경할 게시글의 제목을 나타냅니다.' })
  @IsOptional()
  @IsString()
  readonly title: string;

  @ApiProperty({
    type: ModifyBlockDto,
    isArray: true,
    description: '게시글의 하나 이상의 블록 정보를 나태냅니다.',
  })
  @ArrayNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => ModifyBlockDto)
  blocks: ModifyBlockDto[];
}
