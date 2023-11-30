import { ApiProperty } from '@nestjs/swagger';
import { IsLatLong } from 'class-validator';

export class FindSnapPointQuery {
  @ApiProperty({ description: '' })
  @IsLatLong()
  from: string;

  @IsLatLong()
  to: string;
}
