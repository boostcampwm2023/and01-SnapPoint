import { ApiProperty } from '@nestjs/swagger';
import { File } from '@prisma/client';

export class FileDto {
  @ApiProperty({ description: '파일의 고유 식별자를 나타냅니다.' })
  uuid: string;

  @ApiProperty({ description: '파일에 접근 가능한 URL 주소입니다.' })
  url: string;

  static of(file: File) {
    return {
      uuid: file.uuid,
      url: file.url,
    };
  }
}
