import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { S3 } from 'aws-sdk';

@Injectable()
export class BucketService {
  private bucket: S3;

  constructor(private readonly configService: ConfigService) {
    const NCP_ACCESS_KEY = this.configService.get<string>('NCP_ACCESS_KEY');
    const NCP_SECRET_KEY = this.configService.get<string>('NCP_SECRET_KEY');

    if (!NCP_ACCESS_KEY || !NCP_SECRET_KEY) {
      throw new Error('Authentication not provided for BucketService.');
    }

    const NCP_BUCKET_ENDPOINT = this.configService.get<string>(
      'NCP_BUCKET_ENDPOINT',
    );
    const NCP_BUCKET_REGION =
      this.configService.get<string>('NCP_BUCKET_REGION');

    if (!NCP_BUCKET_ENDPOINT || !NCP_BUCKET_REGION) {
      throw new Error('Bucket info not provided for BucketService.');
    }

    this.bucket = new S3({
      endpoint: NCP_BUCKET_ENDPOINT,
      region: NCP_BUCKET_REGION,
      credentials: {
        accessKeyId: NCP_ACCESS_KEY,
        secretAccessKey: NCP_SECRET_KEY,
      },
    });
  }

  async uploadFile(file: Express.Multer.File) {
    const NCP_BUCKET_NAME = this.configService.get<string>('NCP_BUCKET_NAME');
    if (!NCP_BUCKET_NAME) {
      throw new Error('Bucket info not provided for BucketService.');
    }
    return this.bucket
      .upload({
        Bucket: NCP_BUCKET_NAME,
        Key: file.filename,
        Body: file.buffer,
        ContentType: file.mimetype,
        // TODO: CDN 서비스 구현 후 ACL 권한을 private으로 수정한다.
        ACL: 'public-read',
      })
      .promise();
  }
}
