import { IsUUID } from 'class-validator';

export class CreateBlockFileDto {
  @IsUUID()
  readonly uuid: string;
}
