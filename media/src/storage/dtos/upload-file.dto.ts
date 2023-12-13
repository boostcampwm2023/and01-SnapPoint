import { PassThrough } from 'stream';

export class UploadFileDto {
  name: string;

  format: string;

  stream: PassThrough;
}
