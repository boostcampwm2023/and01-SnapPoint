import { StorageService } from '@/storage/storage.service';
import {
  Injectable,
  InternalServerErrorException,
  Logger,
} from '@nestjs/common';
import * as ffmpeg from 'fluent-ffmpeg';
import { MetadataDto } from './dtos/metadata.dto';
import { ProcessVideoDto } from './dtos/process-video.dto';
import { TargetdataDto } from './dtos/targetdata.dto';

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
   * 비디오가 720p 이상인지 확인합니다.
   * @param width - 비디오의 가로 길이.
   * @param height - 비디오의 세로 길이.
   * @returns 720p 이상 여부.
   */
  private isHigherThan720p(width: number, height: number) {
    return width >= 1280 && height >= 720;
  }

  /**
   * 비디오가 480p 이상인지 확인합니다.
   * @param width - 비디오의 가로 길이.
   * @param height - 비디오의 세로 길이.
   * @returns 480p 이상 여부.
   */
  private isHigherThan480p(width: number, height: number) {
    return width >= 640 && height >= 480;
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

    const longer = Math.max(metadata.width, metadata.height);
    const shorter = Math.min(metadata.width, metadata.height);

    const targets = this.getTargets(longer, shorter);
    const processData = this.getProcessData(metadata, uuid);

    return {
      targets: this.getResizeOptions(targets, longer, metadata.width),
      processData,
    };
  }

  /**
   * 주어진 조건에 따라 대상 해상도 목록을 생성합니다.
   * @param longer - 비디오의 긴 쪽 길이.
   * @param shorter - 비디오의 짧은 쪽 길이.
   * @returns 대상 해상도 목록.
   */
  private getTargets(longer: number, shorter: number): number[] {
    const targets: number[] = [];
    if (this.isHigherThan720p(longer, shorter)) targets.push(720);
    if (this.isHigherThan480p(longer, shorter)) targets.push(480);
    if (!targets.length) targets.push(longer);
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
   * 리사이즈 옵션을 생성합니다.
   * @param targets - 대상 해상도 목록.
   * @param longer - 비디오의 긴 쪽 길이.
   * @param width - 비디오의 가로 길이.
   * @returns 리사이즈 옵션 목록.
   */
  private getResizeOptions(
    targets: number[],
    longer: number,
    width: number,
  ): TargetdataDto[] {
    return targets.map((t) => ({
      quality: t,
      option: longer === width ? `${t}x?` : `?x${t}`,
    }));
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

        const {
          format_name: format,
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
   * @param targetDto - 트랜스코딩 대상 정보.
   * @param processDto - 트랜스코딩 처리 정보.
   */
  transcode(targetDto: TargetdataDto, processDto: ProcessVideoDto) {
    const { quality, option } = targetDto;
    const { uuid, shouldChangeCodec, shouldConvertFormat, shouldReduceFPS } =
      processDto;

    let processStream = ffmpeg(this.storageService.getFileUrl({ name: uuid }));

    if (shouldChangeCodec) {
      processStream = processStream.videoCodec('libx264');
    }

    if (shouldReduceFPS) {
      processStream = processStream.outputOptions('-r 30');
    }

    if (shouldConvertFormat) {
      processStream = processStream.toFormat('mp4');
    }

    processStream
      .addOption('-crf', '23')
      .size(option)
      .outputFormat('mp4')
      .output(`temp/${uuid}_${quality}`)
      .on('end', () => Logger.debug(`transcoded: ${uuid} ${quality}`))
      .on('error', (err) => Logger.error(`transcode error: ${err}`))
      .run();
  }
}
