import { ForbiddenException, Injectable, NotFoundException } from '@nestjs/common';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PostService } from '@/domain/post/post.service';
import { BlockService } from '@/domain/block/block.service';
import { FileService } from '@/domain/file/file.service';
import { ValidationService } from '@/api/validation/validation.service';
import { BlockDto } from '@/domain/block/dtos/block.dto';
import { PostDto } from '@/domain/post/dtos/post.dto';
import { FileDto } from '@/api/file-api/dto/file.dto';
import { Block, File, Post } from '@prisma/client';
import { TransformationService } from '../transformation/transformation.service';
import { FindNearbyPostQuery } from './dtos/find-nearby-post.query.dto';
import { SummaryPostDto } from '@/domain/post/dtos/summary-post.dto';
import { WritePostDto } from './dtos/write-post.dto';
import { CreateBlockDto } from '@/domain/block/dtos/create-block.dto';
import { RedisCacheService } from '@/common/redis/redis-cache.service';

@Injectable()
export class PostApiService {
  constructor(
    private readonly prisma: PrismaProvider,
    private readonly validation: ValidationService,
    private readonly transform: TransformationService,
    private readonly postService: PostService,
    private readonly blockService: BlockService,
    private readonly fileService: FileService,
    private readonly redisService: RedisCacheService,
  ) {}

  private async readPost(post: Post) {
    const blocks = await this.redisService.smembers<Block>(
      `post:${post.uuid}`,
      (s: string) => {
        return JSON.parse(s);
      },
      async (uuid: string) => {
        return await this.blockService.findBlocksWithCoordsByPost(uuid);
      },
    );

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

  async findNearbyPost(findNearbyPostQuery: FindNearbyPostQuery): Promise<SummaryPostDto[]> {
    const findNearbyPostDto = this.transform.toNearbyPostDtoFromQuery(findNearbyPostQuery);

    // TODO: 제대로 된 영역인지 평가한다. (면적, 서비스 범위?)
    const blocks = await this.blockService.findBlocksWithCoordsByArea(findNearbyPostDto);

    const blockWheres = blocks.map((block) => ({ uuid: block.postUuid }));

    const posts = await this.postService.findPosts({ where: { OR: blockWheres } });

    return Promise.all(
      posts.map(async (post) => {
        const postDto = await this.readPost(post);
        const textBlocks = postDto.blocks.filter((block) => block.type === 'text');
        const mediaBlocks = postDto.blocks.filter((block) => block.type === 'media');
        const summary = textBlocks.length > 0 ? textBlocks[0].content : mediaBlocks[0].content;

        postDto.blocks = mediaBlocks;
        return SummaryPostDto.of(postDto, mediaBlocks, summary);
      }),
    );
  }

  async findPost(uuid: string) {
    const post = await this.postService.findPost({ uuid });
    if (!post) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }
    await this.redisService.del(post.uuid);
    return this.readPost(post);
  }

  async writePost(postDto: WritePostDto, userUuid: string) {
    const decomposedPostDto = this.transform.decomposePostRequest(postDto);
    const { post, blocks, files } = decomposedPostDto;

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

  async modifyPost(uuid: string, userUuid: string, postDto: WritePostDto) {
    const decomposedPostDto = this.transform.decomposePostRequest(postDto);
    const { post, blocks, files } = decomposedPostDto;

    const existPost = await this.postService.findPost({ uuid });

    if (!existPost) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }

    if (existPost.userUuid !== userUuid) {
      throw new ForbiddenException('Could not access this post. please check your permission.');
    }

    return this.prisma.beginTransaction(async () => {
      await this.postService.updatePost({ where: { uuid }, data: post });
      await this.redisService.del(uuid);

      const blockMap = new Map<string, CreateBlockDto>();
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
