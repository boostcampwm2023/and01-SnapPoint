import { ApiProperty } from '@nestjs/swagger';
import { IsOptional, IsUUID } from 'class-validator';

export class AttachFileDto {
  @ApiProperty({ description: '업로드한 파일의 식별자(UUID)를 첨부합니다.' })
  @IsUUID()
  readonly uuid: string;

  @ApiProperty({
    description: '업로드한 파일이 영상이라면 썸네일의 식별자(UUID)를 첨부합니다.',
    required: false,
  })
  @IsUUID()
  @IsOptional()
  readonly thumbnailUuid?: string;
}
