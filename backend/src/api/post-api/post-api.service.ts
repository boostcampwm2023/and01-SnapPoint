import { ForbiddenException, Injectable, InternalServerErrorException, NotFoundException } from '@nestjs/common';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PostService } from '@/domain/post/post.service';
import { BlockService } from '@/domain/block/block.service';
import { FileService } from '@/domain/file/file.service';
import { ValidationService } from '@/api/validation/validation.service';
import { BlockDto } from '@/domain/block/dtos/block.dto';
import { PostDto } from '@/domain/post/dtos/post.dto';
import { FileDto } from '@/api/post-api/dtos/file.dto';
import { Block, File, Post, User } from '@prisma/client';
import { TransformationService } from '@/api/transformation/transformation.service';
import { FindNearbyPostQuery } from './dtos/find-nearby-post.query.dto';
import { WritePostDto } from './dtos/write-post.dto';
import { RedisCacheService } from '@/common/redis/redis-cache.service';
import { FindBlocksByPostDto } from '@/domain/block/dtos/find-blocks-by-post.dto';
import { FindFilesBySourceDto } from '@/domain/file/dtos/find-files-by-source.dto';
import { SummarizationService } from '@/api/summarization/summarization.service';
import { UserService } from '@/domain/user/user.service';

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
    private readonly userService: UserService,
  ) {}

  private assemblePost(post: Post, user: User, blocks: Block[], files: File[]): PostDto {
    const blockDtoMap = this.createBlockDtoMap(files, blocks);
    return PostDto.of(post, user, blockDtoMap.get(post.uuid)!);
  }

  private assemblePosts(posts: Post[], users: User[], blocks: Block[], files: File[]): PostDto[] {
    const blockDtoMap = this.createBlockDtoMap(files, blocks);
    return posts.map((post, index) => PostDto.of(post, users[index], blockDtoMap.get(post.uuid)!));
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

  async findEntireBlocksWithPost(posts: Post[]): Promise<Block[][]> {
    const keys = posts.map((post) => `block:${post.uuid}`);
    const entireBlocks = await this.redisService.mget<Block[]>(
      keys,
      (value) => JSON.parse(value),
      async (keys) => {
        const dtos: FindBlocksByPostDto[] = keys.map((key) => {
          const uuid = key.substring('block:'.length);
          return { postUuid: uuid };
        });
        const entireBlocks = await this.blockService.findBlocksByPosts(dtos);

        const blockByUuid = {};

        entireBlocks.forEach((block) => {
          const { postUuid } = block;
          if (!postUuid) {
            throw new InternalServerErrorException();
          }
          if (!blockByUuid[postUuid]) {
            blockByUuid[postUuid] = [];
          }
          blockByUuid[postUuid].push(block);
        });

        const resultArray = dtos.map((dto) => {
          if (!blockByUuid[dto.postUuid]) {
            return [];
          }
          return blockByUuid[dto.postUuid];
        });
        return resultArray;
      },
    );

    if (!entireBlocks) {
      throw new NotFoundException('존재하는 데이터가 없습니다.');
    }

    return entireBlocks;
  }

  async findEntireFilesWithBlocks(blocks: Block[]) {
    const keys = blocks.map((block) => `file:${block.uuid}`);
    const entireFiles = await this.redisService.mget<File>(
      keys,
      (value) => JSON.parse(value),
      async (keys) => {
        const dtos: FindFilesBySourceDto[] = keys.map((key) => {
          const uuid = key.substring('file:'.length);
          return { uuid: uuid };
        });

        const findFiles = await this.fileService.findFilesBySources('block', dtos);

        const fileByUuid = {};

        findFiles.forEach((file) => {
          const { sourceUuid } = file;
          if (!sourceUuid) {
            throw new InternalServerErrorException();
          }
          if (!fileByUuid[sourceUuid]) {
            fileByUuid[sourceUuid] = [];
          }
          fileByUuid[sourceUuid].push(file);
        });

        const resultArray = dtos.map((dto) => {
          if (!fileByUuid[dto.uuid]) {
            return [];
          }
          return fileByUuid[dto.uuid];
        });

        return resultArray;
      },
    );

    if (!entireFiles) {
      throw new NotFoundException('존재하는 데이터가 없습니다.');
    }

    return entireFiles;
  }

  async findNearbyPost(findNearbyPostQuery: FindNearbyPostQuery): Promise<PostDto[]> {
    const findNearbyPostDto = this.transform.toNearbyPostDtoFromQuery(findNearbyPostQuery);

    // 1. 현 위치 주변의 블록을 찾는다.
    const blocks = await this.blockService.findBlocksByArea(findNearbyPostDto);

    // 2. 블록과 연관된 게시글 정보를 찾는다.
    const blockPostUuids = blocks.map((block) => ({ uuid: block.postUuid }));
    const posts = await this.postService.findPosts({ where: { OR: blockPostUuids } });

    const userUuids = posts.map((post) => ({ uuid: post.userUuid }));
    const users = await this.userService.findUsers({ where: { OR: userUuids } });

    // 3. 게시글과 연관된 모든 블록을 찾는다.
    const entireBlocks = ([] as Block[]).concat(...(await this.findEntireBlocksWithPost(posts)));
    // 4. 블록과 연관된 모든 파일을 찾는다.
    const entireFiles = ([] as File[]).concat(...(await this.findEntireFilesWithBlocks(entireBlocks)));

    return this.assemblePosts(posts, users, entireBlocks, entireFiles);
  }

  async findPost(uuid: string, detail: boolean = true) {
    // 1. UUID로 게시글을 찾는다.
    const post = await this.postService.findPost({ uuid });

    if (!post) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }

    const user = await this.userService.findUserByUniqueInput({ uuid: post.userUuid });

    if (!user) {
      throw new NotFoundException(`Cloud not found User with UUID: ${post.userUuid}`);
    }

    if (!detail) {
      return PostDto.of(post, user);
    }

    // 2. 게시글과 연관된 블록들을 찾는다.
    const blocks = await this.blockService.findBlocksByPost({ postUuid: post.uuid });
    // 3. 블록들과 연관된 파일들을 찾는다.
    const files = await this.fileService.findFilesBySources('block', blocks);

    return this.assemblePost(post, user, blocks, files);
  }

  async writePost(postDto: WritePostDto, userUuid: string) {
    const decomposedPostDto = this.transform.decomposePostRequest(postDto);
    const { post, blocks, files } = decomposedPostDto;

    await Promise.all([this.validation.validateBlocks(blocks, files), this.validation.validateFiles(files, userUuid)]);

    return this.prisma.beginTransaction(async () => {
      const summary = await this.summaryService.summarizePost(post.title, blocks);
      const createdPost = await this.postService.createPost(userUuid, { ...post, summary });

      const user = await this.userService.findUserByUniqueInput({ uuid: userUuid });

      if (!user) {
        throw new NotFoundException(`Cloud not found User with UUID: ${userUuid}`);
      }

      const { uuid: postUuid } = createdPost;

      const [createdBlocks, createdFiles] = await Promise.all([
        this.blockService.createBlocks(postUuid, blocks),
        this.fileService.attachFiles(files),
      ]);

      await this.redisService.del(`block:${postUuid}`);
      const deleteCacheKeys = blocks.map((block) => `file:${block.uuid}`);
      await this.redisService.del(deleteCacheKeys);

      return this.assemblePost(createdPost, user, createdBlocks, createdFiles);
    });
  }

  async modifyPost(uuid: string, userUuid: string, postDto: WritePostDto) {
    const decomposedPostDto = this.transform.decomposePostRequest(postDto);
    const { post, blocks, files } = decomposedPostDto;

    const existPost = await this.accessPost(uuid, userUuid);
    const user = await this.userService.findUserByUniqueInput({ uuid: existPost.userUuid });

    if (!user) {
      throw new NotFoundException(`Cloud not found User with UUID: ${existPost.userUuid}`);
    }

    return this.prisma.beginTransaction(async () => {
      const [updatedBlocks, updatedFiles] = await Promise.all([
        this.blockService.modifyBlocks(uuid, blocks),
        this.fileService.modifyFiles(files),
      ]);

      const summary = await this.summaryService.summarizePost(post.title, blocks);

      const updatedPost = await this.postService.updatePost({ where: { uuid }, data: { ...post, summary } });

      await this.validation.validateBlocks(blocks, files), this.redisService.del(`file:${uuid}`);

      await this.redisService.del(`block:${uuid}`);
      const deleteCacheKeys = blocks.map((block) => `file:${block.uuid}`);
      await this.redisService.del(deleteCacheKeys);

      return this.assemblePost(updatedPost, user, updatedBlocks, updatedFiles);
    });
  }

  async deletePost(uuid: string, userUuid: string) {
    const existPost = await this.accessPost(uuid, userUuid);

    return this.prisma.beginTransaction(async () => {
      const [deletedPost, user] = await Promise.all([
        this.postService.deletePost({ uuid }),
        this.userService.findUserByUniqueInput({ uuid: userUuid }),
      ]);

      if (!user) {
        throw new NotFoundException(`Cloud not found User with UUID: ${existPost.userUuid}`);
      }

      const blocks = await this.blockService.deleteBlocksByPost(existPost.uuid);
      const files = await this.fileService.deleteFilesBySources('block', blocks);

      const willDeleteFileKeys = blocks.map(({ uuid }) => `file:${uuid}`);
      await Promise.all([this.redisService.del(`block:${uuid}`), this.redisService.del(willDeleteFileKeys)]);

      return this.assemblePost(deletedPost, user, blocks, files);
    });
  }

  private async accessPost(uuid: string, userUuid: string) {
    const existPost = await this.postService.findPost({ uuid });

    if (!existPost) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }

    if (existPost.userUuid !== userUuid) {
      throw new ForbiddenException('Could not access this post. please check your permission.');
    }
    return existPost;
  }
}
