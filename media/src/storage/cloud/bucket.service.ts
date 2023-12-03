import { Injectable, NotFoundException } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { S3 } from 'aws-sdk';

@Injectable()
export class BucketService {
  private readonly bucket: S3;
  private readonly NCP_BUCKET_NAME: string;

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

    const NCP_BUCKET_NAME = this.configService.get<string>('NCP_BUCKET_NAME');
    if (!NCP_BUCKET_NAME) {
      throw new Error('Bucket info not provided for BucketService.');
    }

    this.NCP_BUCKET_NAME = NCP_BUCKET_NAME;

    this.bucket = new S3({
      endpoint: NCP_BUCKET_ENDPOINT,
      region: NCP_BUCKET_REGION,
      credentials: {
        accessKeyId: NCP_ACCESS_KEY,
        secretAccessKey: NCP_SECRET_KEY,
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
        Bucket: this.NCP_BUCKET_NAME,
        Key: name,
        Body: stream,
        ContentType: format,
        ACL: 'public-read',
      })
      .promise();
  }

  downloadFile(name: string): NodeJS.ReadableStream {
    return this.bucket
      .getObject({ Bucket: this.NCP_BUCKET_NAME, Key: name })
      .createReadStream();
  }
}
