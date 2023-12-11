import {
  ConflictException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { BucketService } from './storages/bucket.service';
import { randomUUID } from 'crypto';
import { UploadedFileDto } from './dtos/uploaded-file.dto';
import { UploadFileURLDto } from './dtos/upload-file-url.dto';
import { UploadFileEndDto } from './dtos/upload-file-end.dto';
import { UploadFileAbortDto } from './dtos/upload-file-abort.dto';
import { UploadFileStartResponseDto } from './dtos/upload-file-start.response.dto';
import { UploadFileEndResponsetDto } from './dtos/upload-file-end.response.dto';

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

  async uploadVideoStart(
    contentType: string,
  ): Promise<UploadFileStartResponseDto> {
    const fileUuid = randomUUID();
    const filePart = await this.bucketService.createMultipartUpload(
      fileUuid,
      contentType,
    );

    if (!filePart.Key || !filePart.UploadId) {
      throw new InternalServerErrorException('파일 저장 시작에 실패했습니다.');
    }

    return UploadFileStartResponseDto.of(filePart.Key, filePart.UploadId);
  }

  async getPresignedUrl(
    uploadFileURLDto: UploadFileURLDto,
  ): Promise<{ presignedUrl: string }> {
    const presignedUrl = await this.bucketService.getPresignedUrl(
      uploadFileURLDto.key,
      uploadFileURLDto.uploadId,
      uploadFileURLDto.partNumber,
    );

    return {
      presignedUrl: presignedUrl,
    };
  }

  async uploadFilePartComplete(
    uploadFilePartDto: UploadFileEndDto,
  ): Promise<UploadFileEndResponsetDto> {
    const { key, uploadId, parts, mimeType } = uploadFilePartDto;
    console.log(uploadFilePartDto);

    try {
      await this.bucketService.listParts(key, uploadId);
    } catch (e) {
      throw new ConflictException('이미 존재하는 파일입니다.');
    }

    const completeUpload = await this.bucketService.completeMultipartUpload(
      key,
      uploadId,
      parts,
    );

    if (!completeUpload.Location) {
      throw new NotFoundException('파일의 경로가 존재하지 않습니다.');
    }

    if (completeUpload.Location.startsWith('http://')) {
      completeUpload.Location = completeUpload.Location.replace(
        'http://',
        'https://',
      );
    }

    return UploadFileEndResponsetDto.of(key, completeUpload.Location, mimeType);
  }

  async uploadFilePartAbort(uploadFileAbortDto: UploadFileAbortDto) {
    const { uploadId, key } = uploadFileAbortDto;

    return await this.bucketService.abortMultipartUpload(key, uploadId);
  }
}
