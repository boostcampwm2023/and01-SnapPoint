import { StorageService } from '@/storage/storage.service';
import { Injectable, InternalServerErrorException } from '@nestjs/common';
import * as ffmpeg from 'fluent-ffmpeg';
import { MetadataDto } from './dtos/metadata.dto';

@Injectable()
export class VideoService {
  constructor(private readonly storageService: StorageService) {}

  private getFramePerSecond(rFrameRate: string): number {
    const [fps, baseSec] = rFrameRate.split('/').map((e) => parseInt(e));
    return fps / baseSec;
  }

  private isHigherThan720p(width: number, height: number) {
    return width >= 1280 && height >= 720;
  }

  private isHigherThan480p(width: number, height: number) {
    return width >= 640 && height >= 480;
  }

  async preprocess(dto: { uuid: string }) {
    const metadata = await this.getMetadata(dto);
    await this.encode(metadata);

    this.storageService.uploadTempFile({
      name: `${dto.uuid}_encoded`,
      format: 'video/mp4',
      path: `temp/${dto.uuid}`,
    });

    const longer = Math.max(metadata.width, metadata.height);
    const shorter = Math.min(metadata.width, metadata.height);

    const shouldResize720P = this.isHigherThan720p(longer, shorter);
    const shouldResize480P = this.isHigherThan480p(longer, shorter);

    return {
      shouldResize720P,
      shouldResize480P,
    };
  }

  private encode(dto: MetadataDto): Promise<void> {
    return new Promise((resolve, reject) => {
      const { uuid, codec, fps, format } = dto;

      const viedoStream = this.storageService.download({ uuid });

      const shouldProcessCodec = codec !== 'h264';
      const shouldReduceFps = fps > 30.0;
      const shouldCovertFormat = !format.toLowerCase().includes('mp4');

      let processStream = ffmpeg().input(viedoStream);

      if (shouldProcessCodec) {
        processStream = processStream.videoCodec('libx264');
      }

      if (shouldReduceFps) {
        processStream = processStream.outputOptions('-r 30');
      }

      if (shouldCovertFormat) {
        processStream = processStream.toFormat('mp4');
      }

      processStream
        .output(`temp/${uuid}`)
        .addOption('-crf', '23')
        .on('end', () => resolve())
        .on('error', (error) => reject(error))
        .run();
    });
  }

  /**
   * 비디오 정보를 받아오고, 메타 데이터를 추출한다.
   */
  private async getMetadata(dto: { uuid: string }): Promise<MetadataDto> {
    return new Promise((resolve, reject) => {
      const { uuid } = dto;

      const url = this.storageService.getFileUrl({ name: uuid });

      ffmpeg.ffprobe(url, (error, data) => {
        if (error || !data) {
          reject(error);
        }

        const streams = data.streams;

        const video = streams.filter(
          (stream) => stream.codec_type === 'video',
        )[0];
        // const audioStream = streams.filter(
        //   (stream) => stream.codec_type === 'audio',
        // )[0];

        if (!video) {
          throw new InternalServerErrorException('비디오 스트림이 없습니다.');
        }

        const { format_name: format } = data.format;

        const {
          r_frame_rate: rFrameRate,
          codec_name: codec,
          width,
          height,
        } = video;

        if (!rFrameRate || !codec || !width || !height || !format) {
          throw new InternalServerErrorException(
            `동영상 ${uuid}에 필요한 정보가 없습니다.`,
          );
        }

        resolve({
          uuid,
          fps: this.getFramePerSecond(rFrameRate),
          width,
          height,
          codec,
          format,
        });
      });
    });
  }
}
