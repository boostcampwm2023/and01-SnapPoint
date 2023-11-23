import { CreatePostApiDto } from './dtos/create-post-api.dto';
import { Injectable, NotFoundException } from '@nestjs/common';
import { PostService } from '@/post/post.service';
import { BlockService } from '@/block/block.service';
import { BlockFileService } from '@/block-file/block-file.service';
import { BlockDto } from '@/block/dtos/block.dto';
import { PostDto } from '@/post/dtos/post.dto';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { FileService } from '@/file/file.service';
import { FileDto } from '@/file/dto/file.dto';
import { CreateBlockDto } from '@/block/dtos/create-block.dto';
import { Block } from '@prisma/client';

@Injectable()
export class PostApiService {
  constructor(
    private postService: PostService,
    private fileService: FileService,
    private blockService: BlockService,
    private blockFileService: BlockFileService,
    private prisma: PrismaProvider,
  ) {}

  isMediaBlock(type: string) {
    if (type === 'image' || type === 'video') {
      return true;
    }
  }

  async readPost(uuid: string) {
    const post = await this.postService.findOne(uuid);

    if (!post) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }

    const blocks = await this.blockService.getOrdered(post.uuid);

    const blockDtos = await Promise.all(
      blocks.map(async (block) => {
        if (!this.isMediaBlock(block.type)) {
          return BlockDto.of(block);
        }
        const blockFiles = await this.blockFileService.findMany({ blockUuid: block.uuid });
        const fileDtos = await Promise.all(
          blockFiles.map(async (blockFile) => {
            const file = await this.fileService.findOne(blockFile.fileUuid);
            if (!file) {
              throw new NotFoundException(`Cloud not found file with UUID: ${uuid}`);
            }
            return FileDto.of(file);
          }),
        );
        return BlockDto.of(block, fileDtos);
      }),
    );

    return PostDto.of(post, blockDtos);
  }

  private async seperateBlockRequest(existBlocks: Block[], blockDtos: CreateBlockDto[]) {
    // TODO: 블록 위치를 효율적으로 저장하는 알고리즘을 도입한다.
    // - 현재는 블록 Index만 기반으로, 블록 order 값들을 모두 변경하는 방식
    // - 효율적으로 order를 정렬하는 방식을 찾아 도입한다.
    const createBlockDtos = [];
    const updateBlockDtos = [];
    const deleteBlockDtos = [];

    const blockMap = new Map();
    const existBlockMap = new Map();

    blockDtos.forEach((blockDto) => {
      if (!blockDto.uuid) {
        createBlockDtos.push(blockDto);
        return;
      }
      blockMap.set(blockDto.uuid, blockDto);
    });
    existBlocks.forEach((existBlock) => existBlockMap.set(existBlock.uuid, existBlock));

    existBlockMap.forEach((blockDto, uuid) => {
      // 기존 Block UUID가 새로운 요청에 없는 경우, 삭제 처리로 간주한다.
      if (!blockMap.has(uuid)) {
        deleteBlockDtos.push(blockDto);
        return;
      }

      // UUID 새로운 요청에 있는 경우, 변경 처리로 간주한다.
      updateBlockDtos.push(blockDto);
    });

    console.log(`create: ${createBlockDtos}`);
    console.log(`update: ${updateBlockDtos}`);
    console.log(`delete: ${deleteBlockDtos}`);
  }

  private async saveBlocks(postUuid: string, blockDtos: CreateBlockDto[]) {
    const savePromises = blockDtos.map(async (blockDto, index) => {
      const { uuid, content, type, latitude, longitude, files: uploadFiles } = blockDto;
      const block = await this.blockService.save(postUuid, { uuid, content, order: index, type, latitude, longitude });

      if (this.isMediaBlock(block.type)) {
        const attachPromises = uploadFiles.map(async (uploadFile) => {
          const file = await this.fileService.findOne(uploadFile.uuid);

          if (!file) {
            throw new NotFoundException(`Cloud not found file with UUID: ${uuid}`);
          }
          return this.blockFileService.save(block.uuid, file);
        });

        await Promise.all(attachPromises);
      }
    });

    return Promise.all(savePromises);
  }

  async write(createPostApiDto: CreatePostApiDto) {
    const { title, blocks } = createPostApiDto;

    const postDto = await this.prisma.beginTransaction(async () => {
      const post = await this.postService.create({ userUuid: '6b781970-a1af-4f72-a7db-dc65ada31d4a', title });
      await this.saveBlocks(post.uuid, blocks);
      return this.readPost(post.uuid);
    });

    return postDto;
  }

  async writeAndPublish(createPostApiDto: CreatePostApiDto) {
    const { title, blocks } = createPostApiDto;

    const postDto = await this.prisma.beginTransaction(async () => {
      const post = await this.postService.create({ userUuid: '6b781970-a1af-4f72-a7db-dc65ada31d4a', title });
      await this.saveBlocks(post.uuid, blocks);

      await this.postService.publish(post.uuid);
      return this.readPost(post.uuid);
    });

    return postDto;
  }

  async save(uuid: string, createPostApiDto: CreatePostApiDto) {
    const postDto = await this.prisma.beginTransaction(async () => {
      const { title, blocks } = createPostApiDto;
      await this.postService.update(uuid, { title });
      await this.saveBlocks(uuid, blocks);
      return this.readPost(uuid);
    });

    return postDto;
  }

  async publish(uuid: string, createPostApiDto: CreatePostApiDto) {
    const postDto = await this.prisma.beginTransaction(async () => {
      const { title, blocks } = createPostApiDto;
      const post = await this.postService.findOne(uuid);

      if (!post) {
        throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
      }

      await this.postService.update(uuid, { title });
      await this.saveBlocks(uuid, blocks);

      await this.postService.publish(uuid);
      return this.readPost(uuid);
    });

    return postDto;
  }
}
