import { StorageService } from '@/storage/storage.service';
import { Injectable } from '@nestjs/common';
import * as sharp from 'sharp';

import { ResizeImageDto } from '@/image/dtos/resize-image.dto';
import { PassThrough } from 'stream';
import { UploadFileDto } from '@/storage/dtos/upload-file.dto';

@Injectable()
export class ImageService {
  constructor(private readonly storageService: StorageService) {}

  private getResizedUploadStream(
    imageStream: NodeJS.ReadableStream,
    quality: number,
  ) {
    const passStream = new PassThrough();
    const sharpStream = sharp().resize(quality).toFormat('webp');
    return imageStream.pipe(sharpStream).pipe(passStream);
  }

  private getUploadDto(
    uuid: string,
    imageStream: NodeJS.ReadableStream,
    quality: number,
  ): UploadFileDto {
    return {
      name: `${uuid}_${quality}p`,
      format: 'image/webp',
      stream: this.getResizedUploadStream(imageStream, quality),
    };
  }

  async resizeImage(resizeImageDto: ResizeImageDto) {
    const { uuid } = resizeImageDto;

    const imageStream = this.storageService.downloadImage({ uuid });

    const uploadDtos: UploadFileDto[] = [
      this.getUploadDto(uuid, imageStream, 144),
      this.getUploadDto(uuid, imageStream, 480),
      this.getUploadDto(uuid, imageStream, 720),
    ];

    const uploadPromises = uploadDtos.map(async (dto) =>
      this.storageService.uploadImage(dto),
    );

    return Promise.all(uploadPromises);
  }
}
