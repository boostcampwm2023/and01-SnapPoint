import { BadRequestException, Injectable } from '@nestjs/common';
import { BucketService } from './storages/bucket.service';
import { randomUUID } from 'crypto';
import { UploadedFileDto } from './dtos/uploaded-file.dto';
import { UploadedFileFieldDto } from './dtos/uploaded-file-field.dto';

@Injectable()
export class UploadService {
  constructor(private readonly bucketService: BucketService) {}

  async uploadFile(file: Express.Multer.File): Promise<UploadedFileDto> {
    const fileUuid = randomUUID();
    file.filename = fileUuid;
    const uploadedFile = await this.bucketService.uploadFile(file);

    return UploadedFileDto.of({
      uuid: fileUuid,
      url: uploadedFile.Location,
      mimeType: file.mimetype,
    });
  }

  async uploadFileField(files: {
    image: Express.Multer.File[];
    video: Express.Multer.File[];
  }): Promise<UploadedFileFieldDto> {
    if (!files.image || !files.video) {
      throw new BadRequestException('이미지 혹은 영상이 존재하지 않습니다.');
    }
    if (files.image.length !== 1 || files.video.length !== 1) {
      throw new BadRequestException('파일의 개수가 1개가 아닙니다.');
    }

    const image = files.image[0];
    const video = files.video[0];

    const imageDto = await this.uploadFile(image);
    const videoDto = await this.uploadFile(video);

    return UploadedFileFieldDto.of(imageDto, videoDto);
  }
}
