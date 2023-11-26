import { ApiProperty } from '@nestjs/swagger';

export class RefreshDto {
  @ApiProperty({ description: '유저 인증을 위한 엑세스 토큰입니다.' })
  readonly accessToken: string;

  static of(accessToken: string): RefreshDto {
    return {
      accessToken,
    };
  }
}
