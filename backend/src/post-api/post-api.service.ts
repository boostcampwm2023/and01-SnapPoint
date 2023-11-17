import { Injectable } from '@nestjs/common';
import { CreatePostApiDto } from './dtos/create-post-api.dto';
import { PostService } from '@/post/post.service';
import { BlockService } from '@/block/block.service';
import { BlockFileService } from '@/block-file/block-file.service';
import { BlockFileDto } from '@/block-file/dtos/block-files.dto';
import { BlockDto } from '@/block/dtos/block.dto';
import { PostDto } from '@/post/dtos/post.dto';
import { PrismaService } from '@/prisma.service';

@Injectable()
export class PostApiService {
  constructor(
    private postService: PostService,
    private blockService: BlockService,
    private blockFileService: BlockFileService,
    private prismaService: PrismaService,
  ) {}

  isMediaBlock(type: string) {
    if (type === 'image' || type === 'video') {
      return true;
    }
  }

  async create(createPostApiDto: CreatePostApiDto): Promise<PostDto> {
    const { title, blocks } = createPostApiDto;
    const userUuid = 'test';

    const createdPost = await this.postService.create({ title, userUuid });

    const blockDtos = await Promise.all(
      blocks.map(async (block, index) => {
        const createdBlock = await this.blockService.create(createdPost.uuid, {
          content: block.content,
          order: index,
          type: block.type,
        });

        const blockFileDtos = [];

        if (this.isMediaBlock(block.type)) {
          block.blockFiles.forEach(async (blockFile) => {
            const createdBlockFile = await this.blockFileService.create(createdBlock.uuid, blockFile);
            blockFileDtos.push(BlockFileDto.of(createdBlockFile));
          });
        }

        const blockDto = BlockDto.of(createdBlock, blockFileDtos);

        return blockDto;
      }),
    );

    return PostDto.of(createdPost, blockDtos);
  }
}
