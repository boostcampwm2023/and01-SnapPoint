import { ApiProperty } from '@nestjs/swagger';
import { IsNumber, IsString } from 'class-validator';

export class Part {
  @IsNumber()
  @ApiProperty({
    description: '전송할때 사용한 순서 번호',
  })
  PartNumber: number;

  @IsString()
  @ApiProperty({
    description: '전송 후 헤더로 받은 ETag',
  })
  ETag: string;
}
