import { PartialType } from '@nestjs/mapped-types';
import { CreateUserDto } from './create-user.dto';
import { IsString, IsStrongPassword } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class UpdateUserDto extends PartialType(CreateUserDto) {
  @IsStrongPassword()
  @ApiProperty({ description: '유저의 비밀번호' })
  readonly password: string;

  @IsString()
  @ApiProperty({ description: '유저의 닉네임' })
  readonly nickname: string;
}
