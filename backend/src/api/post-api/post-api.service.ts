import { ForbiddenException, Injectable, NotFoundException } from '@nestjs/common';
import { ComposedPostDto } from '@/api/post-api/dtos/composed-post.dto';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PostService } from '@/domain/post/post.service';
import { BlockService } from '@/domain/block/block.service';
import { FileService } from '@/domain/file/file.service';
import { ValidationService } from '@/api/validation/validation.service';
import { BlockDto } from '@/domain/block/dtos/block.dto';
import { PostDto } from '@/domain/post/dtos/post.dto';
import { FileDto } from '@/api/file-api/dto/file.dto';
import { WriteBlockDto } from '@/api/post-api/dtos/write-block.dto';
import { File, Post } from '@prisma/client';
import { TransformationService } from '../transformation/transformation.service';
import { FindNearbyPostQuery } from './dtos/find-nearby-post.query.dto';

@Injectable()
export class PostApiService {
  constructor(
    private readonly prisma: PrismaProvider,
    private readonly validation: ValidationService,
    private readonly transform: TransformationService,
    private readonly postService: PostService,
    private readonly blockService: BlockService,
    private readonly fileService: FileService,
  ) {}

  private async readPost(post: Post) {
    const blocks = await this.blockService.findBlocksWithCoordsByPost(post.uuid);
    const fileWheres = blocks.map((block) => ({ sourceUuid: block.uuid }));

    const files = await this.fileService.findFiles({ where: { OR: fileWheres, AND: { source: 'block' } } });

    const fileDtoMap = this.transform.toMapFromArray(
      files,
      (file: File) => file.sourceUuid,
      (file: File) => FileDto.of(file),
    );

    const blockDtos = blocks.map((block) => BlockDto.of(block, fileDtoMap.get(block.uuid)));
    return PostDto.of(post, blockDtos);
  }

  async findNearbyPost(findNearbyPostQuery: FindNearbyPostQuery): Promise<PostDto[]> {
    const findNearbyPostDto = this.transform.toNearbyPostDtoFromQuery(findNearbyPostQuery);

    // TODO: 제대로 된 영역인지 평가한다. (면적, 서비스 범위?)
    const blocks = await this.blockService.findBlocksWithCoordsByArea(findNearbyPostDto);

    const blockWheres = blocks.map((block) => ({ uuid: block.postUuid }));
    const posts = await this.postService.findPosts({ where: { OR: blockWheres } });

    return Promise.all(posts.map((post) => this.readPost(post)));
  }

  async findPost(uuid: string) {
    const post = await this.postService.findPost({ uuid });
    if (!post) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }
    return this.readPost(post);
  }

  async writePost(postDto: ComposedPostDto, userUuid: string) {
    const { post, blocks, files } = postDto;

    await Promise.all([this.validation.validateBlocks(blocks, files), this.validation.validateFiles(files, userUuid)]);

    return this.prisma.beginTransaction(async () => {
      const { uuid: postUuid } = await this.postService.createPost(userUuid, post);
      await this.blockService.createBlocks(postUuid, blocks);

      await Promise.all(
        files.map((file) => {
          const { uuid, source, sourceUuid } = file;
          return this.fileService.updateFile({ where: { uuid }, data: { source, sourceUuid } });
        }),
      );
      return this.findPost(postUuid);
    });
  }

  async modifyPost(uuid: string, userUuid: string, postDto: ComposedPostDto) {
    const { post, blocks, files } = postDto;

    const existPost = await this.postService.findPost({ uuid });

    if (!existPost) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }

    if (existPost.userUuid !== userUuid) {
      throw new ForbiddenException('Could not access this post. please check your permission.');
    }

    return this.prisma.beginTransaction(async () => {
      await this.postService.updatePost({ where: { uuid }, data: post });

      const blockMap = new Map<string, WriteBlockDto>();
      blocks.forEach((block) => blockMap.set(block.uuid, block));

      await this.blockService.deleteBlocks({ postUuid: uuid });
      await Promise.all(
        blocks.map((block) =>
          this.blockService.upsertBlock(uuid, {
            ...block,
            isDeleted: false,
          }),
        ),
      );

      const blockUuids = blocks.map((block) => ({ sourceUuid: block.uuid }));
      await this.fileService.deleteFiles({ OR: blockUuids });
      await Promise.all(
        files.map((file) =>
          this.fileService.updateFile({ where: { uuid: file.uuid }, data: { ...file, isDeleted: false } }),
        ),
      );

      await this.validation.validateBlocks(blocks, files);
      return this.findPost(uuid);
    });
  }
}
