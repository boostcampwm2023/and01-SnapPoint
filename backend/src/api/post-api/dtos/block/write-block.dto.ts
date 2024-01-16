import { ApiProperty } from '@nestjs/swagger';
import { Type } from 'class-transformer';
import { IsString, IsIn, IsLatitude, IsLongitude, IsOptional, ValidateNested } from 'class-validator';
import { AttachFileDto } from '../file/attach-file.dto';

export class WriteBlockDto {
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

  @ApiProperty({
    type: AttachFileDto,
    isArray: true,
    required: false,
    description: '미디어 타입인 경우 필수로 파일 정보를 첨부합니다.',
  })
  @ValidateNested({ each: true })
  @Type(() => AttachFileDto)
  @IsOptional()
  files?: AttachFileDto[];
}
