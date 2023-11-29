import { ApiProperty } from '@nestjs/swagger';
import { IsIn, IsUUID } from 'class-validator';

export class WriteBlockFileDto {
  @ApiProperty({ description: '업로드한 파일의 식별자(UUID)를 첨부합니다.' })
  @IsUUID()
  readonly uuid: string;

  @IsIn(['block'])
  readonly source: string;

  @IsUUID()
  readonly sourceUuid: string;
}
