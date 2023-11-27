import { ApiProperty } from '@nestjs/swagger';

export class LoginDto {
  @ApiProperty({ description: '유저 인증을 위한 엑세스 토큰입니다.' })
  readonly accessToken: string;

  @ApiProperty({ description: '엑세스 토큰 만료시 재발급을 위한 리프레시 토큰입니다.' })
  readonly refreshToken: string;

  static of(accessToken: string, refreshToken: string): LoginDto {
    return {
      accessToken,
      refreshToken,
    };
  }
}
