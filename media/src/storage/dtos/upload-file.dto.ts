export class UploadFileDto {
  name: string;

  format: string;

  stream: NodeJS.WritableStream;
}
