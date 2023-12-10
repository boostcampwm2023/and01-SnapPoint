import { Injectable, Logger, NotFoundException } from '@nestjs/common';
import { BucketService } from '@/storage/cloud/bucket.service';
import { UploadFileDto } from '@/storage/dtos/upload-file.dto';
import { DownloadFileDto } from '@/storage/dtos/download-file.dto';
import { Readable } from 'stream';
import { UploadTempFileDto } from './dtos/upload-temp-file.dto';
import * as fs from 'fs';
import { DeleteFileDto } from './dtos/delete-file.dto';

@Injectable()
export class StorageService {
  constructor(private readonly bucketService: BucketService) {}

  async upload(uploadFileDto: UploadFileDto) {
    const { name, format, stream } = uploadFileDto;
    this.bucketService.uploadFile(name, format, stream);
  }

  async uploadTempFile(uploadFileDto: UploadTempFileDto) {
    const { name, format, path } = uploadFileDto;

    const stream = fs.createReadStream(path);
    await this.bucketService.uploadFile(name, format, stream);
    stream.destroy();
    fs.unlink(path, () => {
      Logger.debug(`deleted: ${path}`);
    });
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

  delete(deleteFileDto: DeleteFileDto) {
    const { uuid } = deleteFileDto;
    return this.bucketService.deleteFile(uuid);
  }

  getFileUrl(dto: { name: string }): string {
    return this.bucketService.getSignedUrl(dto.name);
  }
}
