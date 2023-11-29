import { IsOptional, IsString } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class WritePostDto {
  @ApiProperty({ description: '게시글의 제목을 나타냅니다.' })
  @IsOptional()
  @IsString()
  readonly title: string;
}
