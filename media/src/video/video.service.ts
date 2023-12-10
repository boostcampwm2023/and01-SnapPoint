import { StorageService } from '@/storage/storage.service';
import {
  Injectable,
  InternalServerErrorException,
  Logger,
} from '@nestjs/common';
import * as fs from 'fs';
import * as ffmpeg from 'fluent-ffmpeg';
import { MetadataDto } from './dtos/metadata.dto';
import { ProcessVideoDto } from './dtos/process-video.dto';
import * as path from 'path';
import * as mime from 'mime-types';

@Injectable()
export class VideoService {
  constructor(private readonly storageService: StorageService) {}

  /**
   * 주어진 비율 문자열에서 초당 프레임 수를 계산합니다.
   * @param rFrameRate - 비디오의 r_frame_rate 문자열.
   * @returns 계산된 초당 프레임 수.
   */
  private getFramePerSecond(rFrameRate: string): number {
    const [fps, baseSec] = rFrameRate.split('/').map((e) => parseInt(e));
    return fps / baseSec;
  }

  /**
   * 비디오 코덱이 적절한지 확인합니다.
   * @param codec - 확인할 코덱.
   * @returns 적절한 코덱 여부.
   */
  private hasProperCodec(codec: string) {
    return codec === 'h264';
  }

  /**
   * 초당 프레임 수가 30 이하인지 확인합니다.
   * @param fps - 초당 프레임 수.
   * @returns 30fps 이하 여부.
   */
  private isLowerthan30Fps(fps: number) {
    return fps <= 30.0;
  }

  /**
   * 비디오 포맷이 적절한지 확인합니다.
   * @param formatString - 확인할 포맷 문자열.
   * @returns 적절한 포맷 여부.
   */
  private hasProperFormat(formatString: string) {
    return formatString.toLowerCase().includes('mp4');
  }

  /**
   * UUID를 사용하여 비디오의 메타데이터를 파싱합니다.
   * @param dto - UUID를 포함한 객체.
   * @returns 파싱된 메타데이터.
   */
  async parseMetadata(dto: { uuid: string }) {
    const { uuid } = dto;
    const metadata = await this.getMetadata(
      this.storageService.getFileUrl({ name: uuid }),
    );

    const targets = this.getTargets(metadata.height);
    const processData = this.getProcessData(metadata, uuid);

    return { targets, processData };
  }

  /**
   * 주어진 조건에 따라 대상 해상도 목록을 생성합니다.
   * @param height - 비디오의 세로 크기
   * @returns 대상 해상도 목록.
   */
  private getTargets(height: number): number[] {
    const targets: number[] = [];
    if (height >= 1080) targets.push(1080);
    if (height >= 720) targets.push(720);
    if (height >= 480) targets.push(480);
    if (!targets.length) targets.push(height);
    return targets;
  }

  /**
   * 비디오 메타데이터를 바탕으로 처리할 작업을 결정합니다.
   * @param metadata - 비디오의 메타데이터.
   * @param uuid - 비디오의 UUID.
   * @returns 처리할 작업의 정보.
   */
  private getProcessData(metadata: MetadataDto, uuid: string): ProcessVideoDto {
    return {
      uuid,
      shouldChangeCodec: !this.hasProperCodec(metadata.codec),
      shouldConvertFormat: !this.hasProperFormat(metadata.format),
      shouldReduceFPS: !this.isLowerthan30Fps(metadata.fps),
    };
  }

  /**
   * 주어진 URL에서 비디오 메타데이터를 추출합니다.
   * @param url - 비디오 파일의 URL.
   * @returns 비디오의 메타데이터.
   */
  private async getMetadata(url: string): Promise<MetadataDto> {
    return new Promise((resolve, reject) => {
      ffmpeg.ffprobe(url, (error, data) => {
        if (error || !data) {
          reject(error);
        }

        const video = data.streams.find(
          (stream) => stream.codec_type === 'video',
        );
        if (!video) {
          return reject(
            new InternalServerErrorException('비디오 스트림이 없습니다.'),
          );
        }

        const { format_name: format } = data.format;

        const {
          r_frame_rate: rFrameRate,
          codec_name: codec,
          width,
          height,
        } = video;

        if (!rFrameRate || !codec || !width || !height || !format) {
          return reject(
            new InternalServerErrorException(
              '동영상에 필요한 정보가 없습니다.',
            ),
          );
        }

        resolve({
          fps: this.getFramePerSecond(rFrameRate),
          width,
          height,
          codec,
          format,
        });
      });
    });
  }

  /**
   * 비디오를 트랜스코딩하고, 결과를 저장합니다.
   * @param quality - 트랜스코딩 대상 화질.
   * @param processDto - 트랜스코딩 처리 정보.
   */
  async transcode(quality: number, processDto: ProcessVideoDto): Promise<void> {
    return new Promise((resolve, reject) => {
      const { uuid, shouldChangeCodec, shouldConvertFormat, shouldReduceFPS } =
        processDto;

      let processStream = ffmpeg(
        this.storageService.getFileUrl({ name: uuid }),
      );

      if (shouldChangeCodec) {
        processStream = processStream.videoCodec('libx264');
      }
      if (shouldReduceFPS) {
        processStream = processStream.outputOptions('-r 30');
      }
      if (shouldConvertFormat) {
        processStream = processStream.toFormat('mp4');
      }

      const outputName = `${uuid}_${quality}`;
      const outputFormat = 'mp4';
      const outputPath = `temp/transcode/${outputName}`;

      const width = Math.ceil(quality * (16 / 9));

      processStream
        .addOption('-crf', '23')
        .videoFilters([
          {
            filter: 'scale',
            options: `${width}x${quality}:force_original_aspect_ratio=decrease`,
          },
          {
            filter: 'pad',
            options: `${width}:${quality}:(ow-iw)/2:(oh-ih)/2:black`,
          },
        ])
        .outputFormat(outputFormat)
        .output(outputPath)
        .on('end', async () => {
          Logger.debug(`transcoded: ${uuid} ${quality}`);
          await this.storageService.uploadTempFile({
            name: outputName,
            format: `video/${outputFormat}`,
            path: outputPath,
          });
          resolve();
        })
        .on('error', (err, err2, err3) =>
          reject(
            new InternalServerErrorException(
              `transcode error: ${err} ${err2} ${err3}`,
            ),
          ),
        )
        .run();
    });
  }

  async package(dto: { uuid: string; quality: number }): Promise<void> {
    return new Promise((resolve, reject) => {
      const { uuid, quality } = dto;

      const processStream = ffmpeg(
        this.storageService.getFileUrl({ name: `${uuid}_${quality}` }),
      );

      const outputDir = `temp/package/${uuid}_${quality}`;
      const outputName = `${uuid}_${quality}.m3u8`;
      const outputPath = `${outputDir}/${outputName}`;

      fs.mkdirSync(outputDir);

      processStream
        .addOptions([
          '-profile:v baseline',
          '-level 3.0',
          '-start_number 0',
          '-hls_time 10',
          '-hls_list_size 0',
          '-f hls',
        ])
        .inputFormat('mp4')
        .output(outputPath)
        .on('end', async () => {
          Logger.debug(`packaged: ${uuid}`);

          fs.readdir(outputDir, async (err, files) => {
            if (err) {
              return reject(err);
            }

            const uploadPromises = files.map((fileName) => {
              const filePath = path.join(outputDir, fileName);
              const format = mime.lookup(filePath);

              if (!format) {
                return reject(
                  new InternalServerErrorException(
                    `${fileName} type check failed`,
                  ),
                );
              }

              return this.storageService.uploadTempFile({
                name: fileName,
                format,
                path: filePath,
              });
            });

            await Promise.all(uploadPromises);

            fs.rmdir(outputDir, () => {
              Logger.debug(`${outputDir} deleted`);
            });
          });

          resolve();
        })
        .on('error', (err, err2, err3) =>
          reject(
            new InternalServerErrorException(
              `package error: ${err} ${err2} ${err3}`,
            ),
          ),
        )
        .run();
    });
  }

  async end(dto: { uuid: string; qualities: number[] }) {
    const { uuid, qualities } = dto;

    const BANDWIDTH: { [quailty: number]: number } = {
      480: 2000000,
      720: 4000000,
      1080: 6000000,
    } as const;

    let content = '#EXTM3U';

    qualities.forEach((height) => {
      const width = Math.ceil(height * (16 / 9));

      // 480P 이하인 경우, 500kbps 대역폭
      let bandwidth = BANDWIDTH[height];

      if (!bandwidth) {
        bandwidth = 800000;
      }

      content += `#EXT-X-STREAM-INF:BANDWIDTH=${bandwidth},RESOLUTION=${width}x${height},NAME="${height}p"\n`;
      content += `${uuid}_${height}.m3u8\n`;
    });

    const outputDir = 'temp/playlist';
    const outputName = `${uuid}.m3u8`;

    const outputPath = path.join(outputDir, outputName);

    fs.writeFileSync(outputPath, content);

    await this.storageService.uploadTempFile({
      name: outputName,
      format: 'application/vnd.apple.mpegurl',
      path: outputPath,
    });

    const deletePromises = qualities.map((quality) =>
      this.storageService.delete({ uuid: `${uuid}_${quality}` }),
    );
    await Promise.all(deletePromises);
  }
}
