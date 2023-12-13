export class UploadFileStartResponseDto {
  key: string;

  uploadId: string;

  static of(key: string, uploadId: string): UploadFileStartResponseDto {
    return {
      key: key,
      uploadId: uploadId,
    };
  }
}
