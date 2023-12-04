import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { S3 } from 'aws-sdk';

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
}
