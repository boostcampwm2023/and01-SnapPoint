import { IsString, IsOptional } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';
import { WriteBlockDto } from './write-block.dto';

export class ModifyBlockDto extends WriteBlockDto {
  @ApiProperty({ description: '블록을 수정하는 경우 UUID 식별자를 첨부합니다. 첨부하지 않으면 블록이 생성됩니다.' })
  @IsOptional()
  @IsString()
  readonly uuid: string;
}
