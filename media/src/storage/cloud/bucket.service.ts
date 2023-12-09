import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { S3 } from 'aws-sdk';
import { Readable } from 'stream';

@Injectable()
export class BucketService {
  private readonly bucket: S3;

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

  async uploadFile(
    name: string,
    format: string,
    stream: NodeJS.WritableStream,
  ) {
    return this.bucket
      .upload({
        Bucket: this.configService.getOrThrow<string>('NCP_BUCKET_NAME'),
        Key: name,
        Body: stream,
        ContentType: format,
        ACL: 'public-read',
      })
      .promise();
  }

  downloadFile(name: string): Readable {
    return this.bucket
      .getObject({
        Bucket: this.configService.getOrThrow<string>('NCP_BUCKET_NAME'),
        Key: name,
      })
      .createReadStream();
  }
}
