import { CreateBlockFileDto } from '@/block-file/dtos/create-block-files.dto';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { Type } from 'class-transformer';
import { IsString, IsIn, IsLatitude, IsLongitude, IsOptional, ValidateNested } from 'class-validator';

export class CreateBlockDto {
  @ApiProperty({ description: '블록을 수정하는 경우 UUID 식별자를 첨부합니다. 첨부하지 않으면 블록이 생성됩니다.' })
  @IsOptional()
  @IsString()
  readonly uuid?: string;

  @ApiProperty({ description: '블록을 설명할 수 있는 텍스트입니다.' })
  @IsString()
  readonly content: string;

  @ApiProperty({
    enum: ['text', 'media'],
    description: '텍스트 블록은 문자열 내용만 가질 수 있고, 미디어 블록은 좌표와 파일 정보를 가집니다.',
  })
  @IsIn(['text', 'media'])
  readonly type: string;

  @ApiProperty({ required: false, description: '미디어 타입일 경우 필수로 위도 정보를 첨부합니다.' })
  @IsOptional()
  @IsLatitude()
  readonly latitude?: number;

  @ApiProperty({ required: false, description: '미디어 타입일 경우 필수로 경도 정보를 첨부합니다.' })
  @IsOptional()
  @IsLongitude()
  readonly longitude?: number;

  @ApiPropertyOptional({
    type: CreateBlockFileDto,
    isArray: true,
    required: false,
    description: '미디어 타입일 경우 필수로 한개 이상의 파일 정보를 첨부합니다.',
  })
  @IsOptional()
  @ValidateNested({ each: true })
  @Type(() => CreateBlockFileDto)
  readonly files?: CreateBlockFileDto[];

  order?: number;
}
