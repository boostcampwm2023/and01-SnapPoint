import { IsDate, IsJWT, IsUUID } from 'class-validator';

export class CreateRefreshTokenDto {
  @IsUUID()
  userUuid: string;

  @IsJWT()
  token: string;

  @IsDate()
  expiresAt: Date;
}
