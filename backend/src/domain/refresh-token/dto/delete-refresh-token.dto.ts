import { PartialType, ApiProperty } from '@nestjs/swagger';
import { IsUUID } from 'class-validator';
import { CreateRefreshTokenDto } from './create-refresh-token.dto';

export class DeleteRefreshTokenDto extends PartialType(CreateRefreshTokenDto) {
  @IsUUID()
  @ApiProperty({ description: '유저의 uuid' })
  readonly userUuid: string;
}
