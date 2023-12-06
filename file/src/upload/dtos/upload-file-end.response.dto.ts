export class UploadFileEndResponsetDto {
  uuid: string;

  location: string;

  mimeType: string;

  static of(
    uuid: string,
    location: string,
    mimeType: string,
  ): UploadFileEndResponsetDto {
    return {
      uuid: uuid,
      location: location,
      mimeType: mimeType,
    };
  }
}
