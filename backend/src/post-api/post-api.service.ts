import { Injectable, NotFoundException } from '@nestjs/common';
import { CreatePostApiDto } from './dtos/create-post-api.dto';
import { PostService } from '@/post/post.service';
import { BlockService } from '@/block/block.service';
import { BlockFileService } from '@/block-file/block-file.service';
import { BlockFileDto } from '@/block-file/dtos/block-files.dto';
import { BlockDto } from '@/block/dtos/block.dto';
import { PostDto } from '@/post/dtos/post.dto';
import { PrismaProvider } from '@/prisma.service';

@Injectable()
export class PostApiService {
  constructor(
    private postService: PostService,
    private blockService: BlockService,
    private blockFileService: BlockFileService,
    private prisma: PrismaProvider,
  ) {}

  isMediaBlock(type: string) {
    if (type === 'image' || type === 'video') {
      return true;
    }
  }

  async post(uuid: string): Promise<PostDto> {
    const post = await this.postService.post({ uuid });

    if (!post) {
      throw new NotFoundException();
    }

    const blocks = await this.blockService.blocks({ postUuid: post.uuid });

    const blockDtos = await Promise.all(
      blocks.map(async (block) => {
        const files = await this.blockFileService.blockFiles({ blockUuid: block.uuid });
        const fileDtos = files.map((file) => BlockFileDto.of(file));

        return BlockDto.of(block, fileDtos);
      }),
    );

    return PostDto.of(post, blockDtos);
  }

  async create(createPostApiDto: CreatePostApiDto) {
    const { title, blocks } = createPostApiDto;
    const userUuid = 'test';

    const postDto = await this.prisma.beginTransaction(async () => {
      const createdPost = await this.postService.create({ title, userUuid });

      const blockDtos = await Promise.all(
        blocks.map(async (block, index) => {
          const createdBlock = await this.blockService.create(createdPost.uuid, {
            content: block.content,
            order: index,
            type: block.type,
          });
          if (!this.isMediaBlock(block.type)) {
            return BlockDto.of(createdBlock, []);
          }

          const blockFileDtos = await Promise.all(
            block.files.map(async (blockFile) => {
              const createdBlockFile = await this.blockFileService.create(createdBlock.uuid, blockFile);
              return BlockFileDto.of(createdBlockFile);
            }),
          );
          return BlockDto.of(createdBlock, blockFileDtos);
        }),
      );
      return PostDto.of(createdPost, blockDtos);
    });
    return postDto;
  }
}
