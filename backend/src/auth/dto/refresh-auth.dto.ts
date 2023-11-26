import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty } from 'class-validator';

export class RefreshTokenDto {
  @IsNotEmpty()
  @ApiProperty({ description: '엑세스 토큰 만료시 재발급을 위한 리프레시 토큰입니다.' })
  refresh_token: string;
}
