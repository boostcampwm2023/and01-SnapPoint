export class UploadFileEndResponsetDto {
  uuid: string;

  url: string;

  mimeType: string;

  static of(
    uuid: string,
    location: string,
    mimeType: string,
  ): UploadFileEndResponsetDto {
    return {
      uuid: uuid,
      url: location,
      mimeType: mimeType,
    };
  }
}
