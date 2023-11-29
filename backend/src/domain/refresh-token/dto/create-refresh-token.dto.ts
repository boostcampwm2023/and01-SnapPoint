import { ApiProperty } from '@nestjs/swagger';
import { IsDate, IsJWT, IsUUID } from 'class-validator';

export class CreateRefreshTokenDto {
  @IsUUID()
  @ApiProperty({ description: '특정 유저의 uuid.' })
  userUuid: string;

  @IsJWT()
  @ApiProperty({ description: '특정 유저의 리프레시 토큰.' })
  token: string;

  @IsDate()
  @ApiProperty({ description: '특정 유저의 리프레시 토근의 만료 시간.' })
  expiresAt: Date;
}
