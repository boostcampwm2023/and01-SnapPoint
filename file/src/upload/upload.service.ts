import { Injectable } from '@nestjs/common';
import { BucketService } from './storages/bucket.service';
import { randomUUID } from 'crypto';
import { UploadedFileDto } from './dtos/uploaded-file.dto';

@Injectable()
export class UploadService {
  constructor(private readonly bucketService: BucketService) {}

  async uploadFile(file: Express.Multer.File): Promise<UploadedFileDto> {
    const fileUuid = randomUUID();
    file.filename = fileUuid;
    const uploadedFile = await this.bucketService.uploadFile(file);

    return {
      uuid: fileUuid,
      url: uploadedFile.Location,
      mimeType: file.mimetype,
    };
  }
}
