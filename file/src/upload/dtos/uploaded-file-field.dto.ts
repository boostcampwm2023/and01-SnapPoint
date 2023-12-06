import { UploadedFileDto } from './uploaded-file.dto';

export class UploadedFileFieldDto {
  image: UploadedFileDto;

  video: UploadedFileDto;

  static of(
    image: UploadedFileDto,
    video: UploadedFileDto,
  ): UploadedFileFieldDto {
    return {
      image: image,
      video: video,
    };
  }
}
