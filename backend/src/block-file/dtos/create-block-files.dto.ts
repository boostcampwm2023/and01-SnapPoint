// import { Decimal } from '@prisma/client/runtime/library';
import { IsLatitude, IsLongitude, IsString } from 'class-validator';

export class CreateBlockFileDto {
  @IsString()
  readonly fileContent: string;

  @IsLatitude()
  readonly latitude: number;

  @IsLongitude()
  readonly longitude: number;
}
