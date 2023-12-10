import {
  Inject,
  Injectable,
  InternalServerErrorException,
} from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { ProcessVideoDto } from './dtos/process-video.dto';

@Injectable()
export class VideoManagerService {
  private processStatus = new Map<string, Map<number, boolean>>();

  constructor(@Inject('RMQ_SERVICE') private readonly client: ClientProxy) {}

  emitProcessVideo(
    uuid: string,
    targets: number[],
    processData: ProcessVideoDto,
  ) {
    const status = new Map<number, boolean>();

    targets.forEach((quality) => {
      status.set(quality, false);
      this.client.emit({ cmd: 'video.transcode' }, { quality, processData });
    });

    this.processStatus.set(uuid, status);
  }

  handleVideoProcess(dto: { uuid: string; quality: number }) {
    const { uuid, quality } = dto;

    const status = this.processStatus.get(uuid);

    if (!status) {
      throw new InternalServerErrorException(
        `${uuid}에 관한 처리 정보가 없습니다.`,
      );
    }

    // 패키징된 해상도 정보를 업데이트한다.
    status.set(quality, true);

    let isAllProcessed = true;
    const qualities: number[] = [];

    status.forEach((isProcessed, quality) => {
      isAllProcessed = isAllProcessed && isProcessed;
      qualities.push(quality);
    });

    if (isAllProcessed) {
      this.processStatus.delete(uuid);
      return this.client.emit({ cmd: 'video.end' }, { uuid, qualities });
    }

    this.processStatus.set(uuid, status);
  }
}
