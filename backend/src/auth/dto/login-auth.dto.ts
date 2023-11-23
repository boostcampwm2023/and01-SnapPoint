import { PartialType } from '@nestjs/mapped-types';
import { CreateAuthDto } from './create-auth.dto';
import { IsEmail, IsStrongPassword } from 'class-validator';

export class LoginAuthDto extends PartialType(CreateAuthDto) {
  @IsEmail()
  readonly email: string;

  @IsStrongPassword()
  readonly password: string;
}
