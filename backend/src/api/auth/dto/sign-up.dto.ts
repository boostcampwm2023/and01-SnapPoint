import { ApiProperty } from '@nestjs/swagger';
import { IsEmail, IsString, IsStrongPassword, MinLength } from 'class-validator';

export class SignUpDto {
  @IsEmail()
  @ApiProperty({ description: '유저의 이메일' })
  readonly email: string;

  @IsStrongPassword()
  @ApiProperty({
    description:
      '유저의 비밀번호 { minLength: 8, minLowercase: 1, minUppercase: 1, minNumbers: 1, minSymbols: 1, returnScore: false, pointsPerUnique: 1, pointsPerRepeat: 0.5, pointsForContainingLower: 10, pointsForContainingUpper: 10, pointsForContainingNumber: 10, pointsForContainingSymbol: 10 }',
  })
  readonly password: string;

  @IsString()
  @MinLength(3)
  @ApiProperty({ description: '유저의 닉네임 / 3글자 이상' })
  readonly nickname: string;
}
