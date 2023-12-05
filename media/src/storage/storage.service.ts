import { Injectable, NotFoundException } from '@nestjs/common';
import { BucketService } from '@/storage/cloud/bucket.service';
import { UploadFileDto } from '@/storage/dtos/upload-file.dto';
import { DownloadFileDto } from '@/storage/dtos/download-file.dto';

@Injectable()
export class StorageService {
  constructor(private readonly bucketService: BucketService) {}

  async uploadImage(uploadFileDto: UploadFileDto) {
    const { name, format, stream } = uploadFileDto;
    this.bucketService.uploadFile(name, format, stream);
  }

  downloadImage(downloadFileDto: DownloadFileDto): NodeJS.ReadableStream {
    const { uuid } = downloadFileDto;
    const imageStream = this.bucketService.downloadFile(uuid);
    imageStream.on('error', (e) => {
      if (e) {
        throw new NotFoundException(e);
      }
    });
    return imageStream;
  }
}
