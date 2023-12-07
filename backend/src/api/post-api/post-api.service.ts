import {
  ForbiddenException,
  Injectable,
  InternalServerErrorException,
  Logger,
  NotFoundException,
} from '@nestjs/common';
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
import { HttpService } from '@nestjs/axios';
import { ConfigService } from '@nestjs/config';
import { catchError, firstValueFrom, of } from 'rxjs';
import { AxiosError } from 'axios';

@Injectable()
export class PostApiService {
  constructor(
    private readonly configService: ConfigService,
    private readonly prisma: PrismaProvider,
    private readonly validation: ValidationService,
    private readonly transform: TransformationService,
    private readonly postService: PostService,
    private readonly blockService: BlockService,
    private readonly fileService: FileService,
    private readonly redisService: RedisCacheService,
    private readonly httpService: HttpService,
  ) {}

  async summaryContent(title: string, content: string): Promise<string> {
    console.log(content);
    const { data } = await firstValueFrom(
      this.httpService
        .post(
          'https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize',
          {
            document: { title, content },
            option: { language: 'ko', model: 'general', tone: 3, summaryCount: 1 },
          },
          {
            headers: {
              'X-NCP-APIGW-API-KEY-ID': this.configService.getOrThrow('NCP_AI_CLIENT_ID'),
              'X-NCP-APIGW-API-KEY': this.configService.getOrThrow('NCP_AI_CLIENT_SECRET'),
            },
          },
        )
        .pipe(
          catchError((error: AxiosError) => {
            Logger.error(error.response?.data);
            // 오류 발생 시 null 반환
            return of({ data: { summary: null } });
          }),
        ),
    );

    return data.summary;
  }

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
      const blockUuids = blocks.map((block) => ({ sourceUuid: block.uuid }));
      files = await this.fileService.findFilesBySources('block', blockUuids);

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

  async findNearbyPost(findNearbyPostQuery: FindNearbyPostQuery): Promise<SummaryPostDto[]> {
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
    const blockUuids = entireBlocks.map((block) => ({ sourceUuid: block.uuid }));
    const entireFiles = await this.fileService.findFilesBySources('block', blockUuids);

    const blockMap = this.transform.toMapFromArray<Block, string, Block>(
      entireBlocks,
      (block: Block) => block.postUuid,
      (block: Block) => block,
    );

    const fileDtoMap = this.transform.toMapFromArray<File, string, FileDto>(
      entireFiles,
      (file: File) => file.sourceUuid!,
      (file: File) => FileDto.of(file),
    );

    const postPromises = posts.map(async (post) => {
      const { uuid, title } = post;
      const blocks = blockMap.get(uuid)!;

      const summaryContent = this.blockService.getSummaryContent(blocks);

      let summary: string | null = null;

      if (summaryContent) {
        summary = await this.summaryContent(title, summaryContent);
      }

      if (!summary) {
        summary = this.blockService.getBlockContent(blocks);
      }

      const mediaBlocks = this.blockService.filterMediaBlocks(blocks);
      const blockDtos = mediaBlocks.map((block) => BlockDto.of(block, fileDtoMap.get(block.uuid)));

      return SummaryPostDto.of(post, blockDtos, summary);
    });

    return Promise.all(postPromises);
  }

  async findPost(uuid: string) {
    const post = await this.postService.findPost({ uuid });
    if (!post) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }
    return this.readPost(post);
  }

  async writePost(postDto: WritePostDto, userUuid: string) {
    const decomposedPostDto = this.transform.decomposePostRequest(postDto);
    const { post, blocks, files } = decomposedPostDto;

    await Promise.all([this.validation.validateBlocks(blocks, files), this.validation.validateFiles(files, userUuid)]);

    return this.prisma.beginTransaction(async () => {
      const { uuid: postUuid } = await this.postService.createPost(userUuid, post);
      await this.blockService.createBlocks(postUuid, blocks);
      await this.fileService.attachFiles(files);

      await this.redisService.del(`block:${postUuid}`);
      await this.redisService.del(`file:${postUuid}`);
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

      const blockMap = new Map<string, CreateBlockDto>();
      blocks.forEach((block) => blockMap.set(block.uuid, block));

      await this.blockService.modifyBlocks(uuid, blocks);
      await this.fileService.modifyFiles(files);

      await this.validation.validateBlocks(blocks, files);
      await this.redisService.del(`block:${uuid}`);
      await this.redisService.del(`file:${uuid}`);
      return this.findPost(uuid);
    });
  }
}
