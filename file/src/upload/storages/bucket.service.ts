import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { S3 } from 'aws-sdk';
import { Part } from '../dtos/part.dto';

@Injectable()
export class BucketService {
  private bucket: S3;

  constructor(private readonly configService: ConfigService) {
    this.bucket = new S3({
      endpoint: this.configService.getOrThrow<string>('NCP_BUCKET_ENDPOINT'),
      region: this.configService.getOrThrow<string>('NCP_BUCKET_REGION'),
      credentials: {
        accessKeyId: this.configService.getOrThrow<string>('NCP_ACCESS_KEY'),
        secretAccessKey:
          this.configService.getOrThrow<string>('NCP_SECRET_KEY'),
      },
      signatureVersion: 'v4',
    });
  }

  async uploadFile(file: Express.Multer.File) {
    return this.bucket
      .upload({
        Bucket: this.configService.getOrThrow<string>('NCP_BUCKET_NAME'),
        Key: file.filename,
        Body: file.buffer,
        ContentType: file.mimetype,
        // TODO: CDN 서비스 구현 후 ACL 권한을 private으로 수정한다.
        ACL: 'public-read',
      })
      .promise();
  }

  async createMultipartUpload(key: string, contentType: string) {
    return this.bucket
      .createMultipartUpload({
        Bucket: this.configService.getOrThrow<string>('NCP_BUCKET_NAME'),
        Key: key,
        ContentType: contentType,
        ACL: 'public-read',
      })
      .promise();
  }

  async getPresignedUrl(key: string, uploadId: string, partNumber: number) {
    return this.bucket.getSignedUrl('uploadPart', {
      Bucket: this.configService.getOrThrow<string>('NCP_BUCKET_NAME'),
      Key: key,
      UploadId: uploadId,
      PartNumber: partNumber,
    });
  }

  async completeMultipartUpload(key: string, uploadId: string, parts: Part[]) {
    return this.bucket
      .completeMultipartUpload({
        Bucket: this.configService.getOrThrow<string>('NCP_BUCKET_NAME'),
        Key: key,
        UploadId: uploadId,
        MultipartUpload: {
          Parts: parts,
        },
      })
      .promise();
  }

  async abortMultipartUpload(key: string, uploadId: string) {
    return this.bucket
      .abortMultipartUpload({
        Bucket: this.configService.getOrThrow<string>('NCP_BUCKET_NAME'),
        Key: key,
        UploadId: uploadId,
      })
      .promise();
  }

  async listParts(key: string, uploadId: string) {
    return this.bucket
      .listParts({
        Bucket: this.configService.getOrThrow<string>('NCP_BUCKET_NAME'),
        Key: key,
        UploadId: uploadId,
      })
      .promise();
  }

  async listMultipartUploads() {
    return this.bucket
      .listMultipartUploads({
        Bucket: this.configService.getOrThrow<string>('NCP_BUCKET_NAME'),
      })
      .promise();
  }

  async headObject(key: string) {
    return this.bucket
      .headObject({
        Bucket: this.configService.getOrThrow<string>('NCP_BUCKET_NAME'),
        Key: key,
      })
      .promise();
  }
}
