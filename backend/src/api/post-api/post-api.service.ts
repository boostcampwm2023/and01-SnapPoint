import { ForbiddenException, Injectable, InternalServerErrorException, NotFoundException } from '@nestjs/common';
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
import { WritePostDto } from './dtos/write-post.dto';
import { RedisCacheService } from '@/common/redis/redis-cache.service';
import { SummarizationService } from '../summarization/summarization.service';

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
    private readonly summaryService: SummarizationService,
  ) {}

  private async readPost(post: Post) {
    const blockKey = `block:${post.uuid}`;

    const blocks = await this.redisService.smembers<Block>(
      blockKey,
      (s: string) => {
        return JSON.parse(s);
      },
      async (key: string) => {
        // 캐싱된 값이 없는 경우
        const uuid = key.substring('block:'.length);
        return this.blockService.findBlocksByPost({ postUuid: uuid });
      },
    );

    if (!blocks) {
      throw new InternalServerErrorException('게시물에 블럭이 존재하지 않습니다.');
    }

    const fileKey = `file:${post.uuid}`;
    let files = await this.redisService.smembers<File>(fileKey, (s: string) => {
      return JSON.parse(s);
    });

    if (!files) {
      files = await this.fileService.findFilesBySources('block', blocks);
      await this.redisService.sadd<File>(fileKey, files, 30, (file: File) => JSON.stringify(file));
    }

    const fileDtoMap = this.transform.toMapFromArray<File, string, FileDto>(
      files,
      (file: File) => file.sourceUuid!,
      (file: File) => FileDto.of(file),
    );

    const blockDtos = blocks.map((block) => BlockDto.of(block, fileDtoMap.get(block.uuid)));
    return PostDto.of(post, blockDtos);
  }

  private assemblePost(post: Post, blocks: Block[], files: File[]): PostDto {
    const blockDtoMap = this.createBlockDtoMap(files, blocks);
    return PostDto.of(post, blockDtoMap.get(post.uuid)!);
  }

  private assemblePosts(posts: Post[], blocks: Block[], files: File[]): PostDto[] {
    const blockDtoMap = this.createBlockDtoMap(files, blocks);
    return posts.map((post) => PostDto.of(post, blockDtoMap.get(post.uuid)!));
  }

  private createBlockDtoMap(files: File[], blocks: Block[]) {
    const fileDtoMap = this.transform.toMapFromArray<File, string, FileDto>(
      files,
      (file: File) => file.sourceUuid!,
      (file: File) => FileDto.of(file),
    );

    const blockDtoMap = this.transform.toMapFromArray<Block, string, BlockDto>(
      blocks,
      (block: Block) => block.postUuid,
      (block: Block) => BlockDto.of(block, fileDtoMap.get(block.uuid)),
    );

    return blockDtoMap;
  }

  async findNearbyPost(findNearbyPostQuery: FindNearbyPostQuery): Promise<PostDto[]> {
    const findNearbyPostDto = this.transform.toNearbyPostDtoFromQuery(findNearbyPostQuery);

    // 1. 현 위치 주변의 블록을 찾는다.
    const blocks = await this.blockService.findBlocksByArea(findNearbyPostDto);

    // 2. 블록과 연관된 게시글 정보를 찾는다.
    const blockPostUuids = blocks.map((block) => ({ uuid: block.postUuid }));
    const posts = await this.postService.findPosts({ where: { OR: blockPostUuids } });

    // 3. 게시글과 연관된 모든 블록을 찾는다.
    const postUuids = posts.map((post) => ({ postUuid: post.uuid }));
    const entireBlocks = await this.blockService.findBlocksByPosts(postUuids);

    // 4. 블록과 연관된 모든 파일을 찾는다.
    const entireFiles = await this.fileService.findFilesBySources('block', blocks);

    return this.assemblePosts(posts, entireBlocks, entireFiles);
  }

  async findPost(uuid: string) {
    // 1. UUID로 게시글을 찾는다.
    const post = await this.postService.findPost({ uuid });

    if (!post) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }

    // 2. 게시글과 연관된 블록들을 찾는다.
    const blocks = await this.blockService.findBlocksByPost({ postUuid: post.uuid });
    // 3. 블록들과 연관된 파일들을 찾는다.
    const files = await this.fileService.findFilesBySources('block', blocks);

    return this.assemblePost(post, blocks, files);
  }

  async writePost(postDto: WritePostDto, userUuid: string) {
    const decomposedPostDto = this.transform.decomposePostRequest(postDto);
    const { post, blocks, files } = decomposedPostDto;

    await Promise.all([this.validation.validateBlocks(blocks, files), this.validation.validateFiles(files, userUuid)]);

    return this.prisma.beginTransaction(async () => {
      const summary = await this.summaryService.summarizePost(post.title, blocks);
      const createdPost = await this.postService.createPost(userUuid, { ...post, summary });

      const { uuid: postUuid } = createdPost;

      const [createdBlocks, createdFiles] = await Promise.all([
        this.blockService.createBlocks(postUuid, blocks),
        this.fileService.attachFiles(files),
      ]);

      await this.redisService.del(`block:${postUuid}`);
      await this.redisService.del(`file:${postUuid}`);
      return this.assemblePost(createdPost, createdBlocks, createdFiles);
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
      const [summary, updatedBlocks, updatedFiles] = await Promise.all([
        this.summaryService.summarizePost(post.title, blocks),
        this.blockService.modifyBlocks(uuid, blocks),
        this.fileService.modifyFiles(files),
      ]);

      const updatedPost = await this.postService.updatePost({ where: { uuid }, data: { ...post, summary } });

      await Promise.all([
        this.validation.validateBlocks(blocks, files),
        this.redisService.del(`block:${uuid}`),
        this.redisService.del(`file:${uuid}`),
      ]);

      return this.assemblePost(updatedPost, updatedBlocks, updatedFiles);
    });
  }
}
