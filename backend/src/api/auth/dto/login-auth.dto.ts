import { PartialType } from '@nestjs/mapped-types';
import { CreateAuthDto } from './create-auth.dto';
import { IsEmail, IsStrongPassword } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class LoginAuthDto extends PartialType(CreateAuthDto) {
  @IsEmail()
  @ApiProperty({ description: '유저의 이메일' })
  readonly email: string;

  @IsStrongPassword()
  @ApiProperty({ description: '유저의 비밀번호' })
  readonly password: string;
}
