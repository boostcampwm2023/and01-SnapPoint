import { Injectable } from '@nestjs/common';
import { FindNearbyPostQuery } from '../post-api/dtos/find-nearby-post.query.dto';
import { randomUUID } from 'crypto';
import { plainToInstance } from 'class-transformer';
import { DecomposedPostDto } from '../post-api/dtos/decomposed-post.dto';
import { ModifyPostDto } from '../post-api/dtos/post/modify-post.dto';
import { AttachFileDto } from '../post-api/dtos/file/attach-file.dto';
import { DecomposedBlockDto } from './dtos/decomposed-block.dto';
import { DecomposedPostDataDto } from './dtos/decomposed-post-data.dto';
import { DecomposedFileDto } from './dtos/decomposed-file.dto';
import { WritePostDto } from '../post-api/dtos/post/write-post.dto';

@Injectable()
export class TransformationService {
  private parseLatLon(latLon: string) {
    const [lat, lon] = latLon.split(',').map((s) => parseFloat(s.trim()));
    return { latitude: lat, longitude: lon };
  }

  toNearbyPostDtoFromQuery(query: FindNearbyPostQuery) {
    const { from, to } = query;

    const { latitude: fromLat, longitude: fromLon } = this.parseLatLon(from);
    const { latitude: toLat, longitude: toLon } = this.parseLatLon(to);

    return {
      latitudeMin: Math.min(fromLat, toLat),
      latitudeMax: Math.max(fromLat, toLat),
      longitudeMin: Math.min(fromLon, toLon),
      longitudeMax: Math.max(fromLon, toLon),
    };
  }

  toMapFromArray<T, K, V>(items: T[], extractKeyFn: (item: T) => K, transformFn: (item: T) => V): Map<K, V[]> {
    const map = new Map<K, V[]>();
    items.forEach((item) => {
      const key = extractKeyFn(item);
      const collection = map.get(key) || [];
      collection.push(transformFn(item));
      map.set(key, collection);
    });
    return map;
  }

  /**
   * 게시글 파일의 썸네일 유무에 따라 분할해 반환한다.
   * @param files 게시글의 파일 데이터들
   * @param blockUuid 블록의 UUID
   * @returns 분할된 파일 데이터
   */
  private decomposeAttachFiles(files: AttachFileDto[], blockUuid: string) {
    return files.flatMap(({ uuid, thumbnailUuid }) => {
      // 썸네일이 없는 사진 파일은 바로 반환한다.
      if (!thumbnailUuid) {
        return plainToInstance(DecomposedFileDto, { uuid: uuid, source: 'block', sourceUuid: blockUuid });
      }

      // 썸네일을 가진 동영상 파일은 썸네일 사진, 그리고 동영상 정보를 분리해 반환한다.
      return [
        plainToInstance(DecomposedFileDto, {
          uuid: uuid,
          source: 'block',
          sourceUuid: blockUuid,
          thumbnailUuid: thumbnailUuid,
        }),
        plainToInstance(DecomposedFileDto, {
          uuid: thumbnailUuid,
          source: 'block',
          sourceUuid: blockUuid,
        }),
      ];
    });
  }

  /**
   * 게시글, 블록, 파일 구조의 게시글 데이터를 도메인 별로 분할해 반환한다.
   * @param postDto 분할할 게시글 데이터
   * @param postUuid 게시글의 UUID
   * @returns 분할된 게시글, 블록, 파일 데이터의 집합
   */
  decomposePostData(postDto: WritePostDto | ModifyPostDto, postUuid?: string): DecomposedPostDataDto {
    const blockDtos: DecomposedBlockDto[] = [];
    const fileDtos: DecomposedFileDto[] = [];

    const { title, blocks } = postDto;

    blocks.forEach((block, order) => {
      const { content, type, latitude, longitude, files } = block;

      // 블록의 UUID가 없으면 임시로 생성한다.
      const blockUuid = block['uuid'] ?? randomUUID();

      if (files) {
        fileDtos.push(...this.decomposeAttachFiles(files, blockUuid));
      }

      blockDtos.push(
        plainToInstance(DecomposedBlockDto, { uuid: blockUuid, postUuid, content, type, latitude, longitude, order }),
      );
    });

    return plainToInstance(DecomposedPostDataDto, {
      post: plainToInstance(DecomposedPostDto, { uuid: postUuid ?? randomUUID(), title }),
      blocks: blockDtos,
      files: fileDtos,
    });
  }
}
