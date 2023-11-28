import { Injectable } from '@nestjs/common';
import { S3 } from 'aws-sdk';
import { config } from 'dotenv';

config();
const { NCP_ACCESS_KEY, NCP_SECRET_KEY, NCP_BUCKET_ENDPOINT, NCP_BUCKET_REGION, NCP_BUCKET_NAME } = process.env;

@Injectable()
export class BucketService {
  private bucket: S3;

  constructor() {
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
