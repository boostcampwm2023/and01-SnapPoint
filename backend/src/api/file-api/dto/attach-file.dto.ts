import { IsIn, IsUUID } from 'class-validator';

export class AttachFileDto {
  @IsIn(['block'])
  readonly source: string;

  @IsUUID()
  readonly sourceUuid: string;
}
