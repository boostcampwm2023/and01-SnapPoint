import { Injectable } from '@nestjs/common';
import { FindNearbyPostQuery } from '../post-api/dtos/find-nearby-post.query.dto';
import { WritePostDto } from '../post-api/dtos/write-post.dto';
import { randomUUID } from 'crypto';
import { CreateBlockDto } from '@/domain/block/dtos/create-block.dto';
import { CreatePostDto } from '@/domain/post/dtos/create-post.dto';
import { plainToInstance } from 'class-transformer';
import { UpdateFileDto } from '@/domain/file/dtos/update-file.dto';
import { DecomposedPostDto } from '../post-api/dtos/decomposed-post.dto';

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

  decomposePostRequest(postDto: WritePostDto) {
    const fileDtos: UpdateFileDto[] = [];

    const blockDtos = postDto.blocks.map((block, order) => {
      const { uuid, content, type, latitude, longitude, files } = block;
      const blockUuid = uuid ? uuid : randomUUID();

      if (files) {
        fileDtos.push(
          ...files.map((file) =>
            plainToInstance(UpdateFileDto, { uuid: file.uuid, source: 'block', sourceUuid: blockUuid }),
          ),
        );
      }

      return plainToInstance(CreateBlockDto, { uuid: blockUuid, content, type, latitude, longitude, order });
    });

    return plainToInstance(DecomposedPostDto, {
      post: plainToInstance(CreatePostDto, { title: postDto.title }),
      blocks: blockDtos,
      files: fileDtos,
    });
  }
}
