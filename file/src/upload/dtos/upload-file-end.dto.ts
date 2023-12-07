import { ApiProperty } from '@nestjs/swagger';
import { IsString } from 'class-validator';
import { Part } from './part.dto';

export class UploadFileEndDto {
  @IsString()
  @ApiProperty({ description: '업로드 완료를 위한 key' })
  key: string;

  @IsString()
  @ApiProperty({ description: '업로드 완료를 위한 ID' })
  uploadId: string;

  @IsString()
  @ApiProperty({ description: '업로드 파일의 mimeType' })
  mimeType: string;

  @ApiProperty({
    description: 'PreSignedURL로 파일 전송 후 받은 ETag, PartNumber',
  })
  parts: Part[];
}
