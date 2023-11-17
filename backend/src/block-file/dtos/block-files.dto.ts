import { BlockFile } from '@prisma/client';
import { IsLatitude, IsLongitude, IsString } from 'class-validator';

export class BlockFileDto {
  @IsString()
  readonly fileURL: string;

  @IsLatitude()
  readonly latitude: number;

  @IsLongitude()
  readonly longitude: number;

  static of(blockFile: BlockFile): BlockFileDto {
    return {
      fileURL: `test/${blockFile.fileName}`,
      latitude: blockFile.latitude,
      longitude: blockFile.longitude,
    };
  }
}
