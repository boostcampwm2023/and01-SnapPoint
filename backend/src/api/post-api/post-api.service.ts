import { ForbiddenException, Injectable, NotFoundException } from '@nestjs/common';
import { ComposedPostDto } from './dtos/composed-post.dto';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { PostService } from '@/domain/post/post.service';
import { BlockService } from '@/domain/block/block.service';
import { FileService } from '@/domain/file/file.service';
import { ValidationService } from '../validation/validation.service';
import { BlockDto } from '@/domain/block/dtos/block.dto';
import { PostDto } from '@/domain/post/dtos/post.dto';
import { FileDto } from '../file-api/dto/file.dto';
import { WriteBlockDto } from './dtos/write-block.dto';

@Injectable()
export class PostApiService {
  constructor(
    private readonly prisma: PrismaProvider,
    private readonly validation: ValidationService,
    private readonly postService: PostService,
    private readonly blockService: BlockService,
    private readonly fileService: FileService,
  ) {}

  async readPost(uuid: string) {
    const post = await this.postService.findPost({ uuid });
    if (!post) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }

    const blocks = await this.blockService.findBlocks({ where: { postUuid: post.uuid } });
    const blockPromises = blocks.map(async (block) => {
      const files = await this.fileService.findFiles({ where: { source: 'block', sourceUuid: block.uuid } });
      const fileDtos = files.map((file) => FileDto.of(file));
      return BlockDto.of(block, fileDtos);
    });

    const blockDtos = await Promise.all(blockPromises);
    return PostDto.of(post, blockDtos);
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
      return this.readPost(postUuid);
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
          this.blockService.upsertBlock({
            where: { uuid: block.uuid },
            data: { ...block, postUuid: uuid, isDeleted: false },
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
      return this.readPost(uuid);
    });
  }
}
