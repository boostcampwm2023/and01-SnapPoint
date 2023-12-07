import { ApiProperty } from '@nestjs/swagger';
import { IsString } from 'class-validator';

export class UploadFileAbortDto {
  @IsString()
  @ApiProperty({ description: '업로드 취소를 위한 key' })
  key: string;

  @IsString()
  @ApiProperty({ description: '업로드 취소를 위한 ID' })
  uploadId: string;
}
