import { Injectable, NotFoundException } from '@nestjs/common';
import { BucketService } from '@/storage/cloud/bucket.service';
import { UploadFileDto } from '@/storage/dtos/upload-file.dto';
import { DownloadFileDto } from '@/storage/dtos/download-file.dto';
import { Readable } from 'stream';

@Injectable()
export class StorageService {
  constructor(private readonly bucketService: BucketService) {}

  async upload(uploadFileDto: UploadFileDto) {
    const { name, format, stream } = uploadFileDto;
    this.bucketService.uploadFile(name, format, stream);
  }

  download(downloadFileDto: DownloadFileDto): Readable {
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
