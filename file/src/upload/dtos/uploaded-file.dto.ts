export class UploadedFileDto {
  uuid: string;

  url: string;

  mimeType: string;

  static of({ uuid, url, mimeType }): UploadedFileDto {
    return {
      uuid: uuid,
      url: url,
      mimeType: mimeType,
    };
  }
}
