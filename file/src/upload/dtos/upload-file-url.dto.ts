import { ApiProperty } from '@nestjs/swagger';
import { IsNumber, IsString } from 'class-validator';

export class UploadFileURLDto {
  @IsString()
  @ApiProperty({ description: '업로드 URL을 받기 위한 key' })
  key: string;

  @IsString()
  @ApiProperty({ description: '업로드 URL을 받기 위한 ID' })
  uploadId: string;

  @IsNumber()
  @ApiProperty({ description: '이번에 보낼 파일의 순서 번호' })
  partNumber: number;
}
