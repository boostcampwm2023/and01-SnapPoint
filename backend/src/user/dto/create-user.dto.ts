import { ApiProperty } from '@nestjs/swagger';
import { IsEmail, IsString, IsStrongPassword } from 'class-validator';

export class CreateUserDto {
  @IsEmail()
  @ApiProperty({ description: '유저의 이메일' })
  readonly email: string;

  @IsStrongPassword()
  @ApiProperty({ description: '유저의 비밀번호' })
  readonly password: string;

  @IsString()
  @ApiProperty({ description: '유저의 닉네임' })
  readonly nickname: string;
}
