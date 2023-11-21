import { File } from '@prisma/client';
import { config } from 'dotenv';

config();

export class FileDto {
  uuid: string;

  url: string;

  static of(file: File) {
    return {
      uuid: file.uuid,
      url: file.url,
    };
  }
}
