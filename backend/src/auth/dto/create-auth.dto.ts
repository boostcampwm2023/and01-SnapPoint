import { IsEmail, IsString, IsStrongPassword } from 'class-validator';

export class CreateAuthDto {
  @IsEmail()
  readonly email: string;

  @IsStrongPassword()
  readonly password: string;

  @IsString()
  readonly nickname: string;
}
