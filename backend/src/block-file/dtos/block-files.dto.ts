import { BlockFile } from '@prisma/client';

export class BlockFileDto {
  readonly fileURL: string;

  readonly latitude: number;

  readonly longitude: number;

  static of(blockFile: BlockFile): BlockFileDto {
    return {
      fileURL: `test/${blockFile.fileName}`,
      latitude: blockFile.latitude,
      longitude: blockFile.longitude,
    };
  }
}
