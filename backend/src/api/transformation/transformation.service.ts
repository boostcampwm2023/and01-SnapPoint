import { Injectable } from '@nestjs/common';
import { FindNearbyPostQuery } from '../post-api/dtos/find-nearby-post.query.dto';
import { randomUUID } from 'crypto';
import { plainToInstance } from 'class-transformer';
import { AttachFileDto } from '../post-api/dtos/file/attach-file.dto';
import { DecomposedBlockDto } from './dtos/decomposed-block.dto';
import { DecomposedPostDataDto } from './dtos/decomposed-post-data.dto';
import { DecomposedFileDto } from './dtos/decomposed-file.dto';
import { DecomposedPostDto } from './dtos/decomposed-post.dto';
import { WritePostDto } from '../post-api/dtos/post/write-post.dto';
import { ModifyPostDto } from '../post-api/dtos/post/modify-post.dto';
import { UserPayload } from '@/common/guards/user-payload.interface';
import { BlockDto } from '@/domain/block/dtos/block.dto';
import { PostDto } from '@/domain/post/dtos/post.dto';
import { Post, Block, File } from '@prisma/client';
import { FileDto } from '../post-api/dtos/file.dto';
import { UtilityService } from '@/common/utility/utility.service';

@Injectable()
export class TransformationService {
  constructor(private readonly utils: UtilityService) {}

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
  decomposePostData(postDataDto: WritePostDto | ModifyPostDto, postUuid?: string): DecomposedPostDataDto {
    const blockDtos: DecomposedBlockDto[] = [];
    const fileDtos: DecomposedFileDto[] = [];

    const { title, blocks } = postDataDto;
    const postDto = plainToInstance(DecomposedPostDto, { uuid: postUuid ?? randomUUID(), title });

    blocks.forEach((block, order) => {
      const { content, type, latitude, longitude, files } = block;

      // 블록의 UUID가 없으면 임시로 생성한다.
      const uuid = block['uuid'] ?? randomUUID();

      // 파일이 있는 경우 파일 DTO를 해석하고 추가한다.
      if (files) {
        fileDtos.push(...this.decomposeAttachFiles(files, uuid));
      }

      const blockDto = { uuid, content, type, latitude, longitude, order, postUuid: postDto.uuid };
      blockDtos.push(plainToInstance(DecomposedBlockDto, blockDto));
    });

    return plainToInstance(DecomposedPostDataDto, { post: postDto, blocks: blockDtos, files: fileDtos });
  }

  /**
   * 게시글, 사용자, 블록, 파일 정보를 받아 계층적인 게시글 데이터 구조로 변환한다.
   * @param post 게시글 엔티티
   * @param user 사용자 정보 (닉네임, 이메일)
   * @param blocks 블록 엔티티 배열
   * @param files 파일 엔티티 배열
   * @returns 게시글, 블록, 파일 순으로 계층화된 데이터
   */
  assemblePost(post: Post, user: UserPayload, blocks: Block[], files: File[]): PostDto {
    const blockDtoMap = this.createBlockDtoMap(files, blocks);
    return PostDto.of(post, user, blockDtoMap.get(post.uuid)!);
  }

  /**
   * 여러 게시글의 사용자, 블록, 파일 정보를 받아 계층적인 게시글 데이터 구조로 변환한다.
   * @param post 게시글 엔티티
   * @param user 사용자 정보 (닉네임, 이메일)
   * @param blocks 블록 엔티티 배열
   * @param files 파일 엔티티 배열
   * @returns 게시글, 블록, 파일 순으로 계층화된 데이터 배열
   */
  assemblePosts(posts: Post[], users: UserPayload[], blocks: Block[], files: File[]): PostDto[] {
    const blockDtoMap = this.createBlockDtoMap(files, blocks);
    return posts.map((post, index) => PostDto.of(post, users[index], blockDtoMap.get(post.uuid)!));
  }

  /**
   * 블록 및 파일 정보를 받아, Map 자료 구조로 반환한다.
   * @param files 파일 엔티티 배열
   * @param blocks 블록 엔티티 배열
   * @returns 블록과 파일 DTO로 구조와된 Map
   */
  private createBlockDtoMap(files: File[], blocks: Block[]) {
    const fileDtoMap = this.utils.toTransMapFromArray<File, string, FileDto>(
      files,
      (file: File) => file.sourceUuid!,
      (file: File) => FileDto.of(file),
    );

    const blockDtoMap = this.utils.toTransMapFromArray<Block, string, BlockDto>(
      blocks,
      (block: Block) => block.postUuid,
      (block: Block) => BlockDto.of(block, fileDtoMap.get(block.uuid)),
    );

    return blockDtoMap;
  }
}
