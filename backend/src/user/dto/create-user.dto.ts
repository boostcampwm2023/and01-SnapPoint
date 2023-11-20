import { IsEmail, IsString, IsStrongPassword } from 'class-validator';

export class CreateUserDto {
  @IsEmail()
  readonly email: string;

  @IsStrongPassword()
  readonly password: string;

  @IsString()
  readonly nickname: string;
}
