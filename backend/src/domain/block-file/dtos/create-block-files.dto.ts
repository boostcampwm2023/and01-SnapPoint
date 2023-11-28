import { ApiProperty } from '@nestjs/swagger';
import { IsUUID } from 'class-validator';

export class CreateBlockFileDto {
  @ApiProperty({ description: '업로드한 파일의 식별자(UUID)를 첨부합니다.' })
  @IsUUID()
  readonly uuid: string;
}
