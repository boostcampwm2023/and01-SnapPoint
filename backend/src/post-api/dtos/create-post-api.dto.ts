import { CreateBlockDto } from '@/block/dtos/create-block.dto';
import { Type } from 'class-transformer';
import { ArrayNotEmpty, IsOptional, IsString, ValidateNested } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class CreatePostApiDto {
  @ApiProperty({ description: '게시글의 제목을 나타냅니다.' })
  @IsOptional()
  @IsString()
  readonly title: string;

  @ApiProperty({
    type: CreateBlockDto,
    isArray: true,
    description: '게시글의 하나 이상의 블록 정보를 나태냅니다.',
  })
  @ArrayNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => CreateBlockDto)
  readonly blocks: CreateBlockDto[];
}
