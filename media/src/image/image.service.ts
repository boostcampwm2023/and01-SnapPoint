import { StorageService } from '@/storage/storage.service';
import { Injectable } from '@nestjs/common';
import * as sharp from 'sharp';

import { ResizeImageDto } from '@/image/dtos/resize-image.dto';
import { PassThrough } from 'stream';

@Injectable()
export class ImageService {
  constructor(private readonly storageService: StorageService) {}

  async resizeImage(resizeImageDto: ResizeImageDto) {
    const { uuid } = resizeImageDto;

    const imageStream = this.storageService.downloadImage({ uuid });
    const sharpStream = sharp();
    const passStream = new PassThrough();

    const fileName = `${uuid}_720p`;

    imageStream.pipe(sharpStream).resize(720).toFormat('webp').pipe(passStream);

    return this.storageService.uploadImage({
      name: fileName,
      format: 'image/webp',
      stream: passStream,
    });
  }
}
