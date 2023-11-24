import { FileDto } from '@/file/dto/file.dto';
import { ApiProperty } from '@nestjs/swagger';
import { Block } from '@prisma/client';

export class BlockDto {
  @ApiProperty({ description: '블록의 고유 식별자입니다.' })
  readonly uuid: string;

  @ApiProperty({ description: '블록을 설명하는 문자열 데이터입니다.' })
  readonly content: string;

  @ApiProperty({ enum: ['text', 'media'], description: '미디어 타입은 좌표와 파일 정보를 추가로 가집니다.' })
  readonly type: string;

  @ApiProperty({ description: '미디어 블록일 경우 위도 정보를 추가로 반환합니다.', required: false })
  readonly latitude?: number;

  @ApiProperty({ description: '미디어 블록일 경우 경도 정보를 추가로 반환합니다.', required: false })
  readonly longitude?: number;

  @ApiProperty({
    type: [FileDto],
    description: '미디어 블록일 경우 파일들의 정보를 추가로 반환합니다.',
    required: false,
  })
  readonly files?: FileDto[];

  static of(block: Block, files?: FileDto[]): BlockDto {
    if (block.type === 'text') {
      return {
        uuid: block.uuid,
        content: block.content,
        type: block.type,
      };
    }

    return {
      uuid: block.uuid,
      content: block.content,
      latitude: block.latitude,
      longitude: block.longitude,
      type: block.type,
      files: files,
    };
  }
}
