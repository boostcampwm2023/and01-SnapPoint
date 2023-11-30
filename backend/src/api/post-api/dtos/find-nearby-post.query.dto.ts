import { ApiProperty } from '@nestjs/swagger';
import { IsLatLong } from 'class-validator';

export class FindNearbyPostQuery {
  @ApiProperty({ description: '조회하고 싶은 위치의 시작 지점을 나타냅니다.', example: '7.7839,-74.3538' })
  @IsLatLong()
  from: string;

  @ApiProperty({ description: '조회하고 싶은 위치의 끝 지점을 나타냅니다.', example: '9.7795,-70.0012' })
  @IsLatLong()
  to: string;
}
