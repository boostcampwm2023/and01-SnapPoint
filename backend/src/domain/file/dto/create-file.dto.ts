import { ApiProperty } from '@nestjs/swagger';

export class CreateFileDto {
  @ApiProperty({ description: '업로드할 파일의 바이너리 데이터입니다.' })
  readonly file: Express.Multer.File;
}
