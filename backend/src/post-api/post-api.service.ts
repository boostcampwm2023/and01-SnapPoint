import { PrismaService } from '@/prisma.service';
import { Injectable } from '@nestjs/common';
import { Block, Post } from '@prisma/client';
import { CreatePostApiDto } from './dtos/create-post-api.dto';
import { PostService } from '@/post/post.service';
import { BlockService } from '@/block/block.service';
import { BlockFileService } from '@/block-file/block-file.service';

@Injectable()
export class PostApiService {
  constructor(
    private postService: PostService,
    private blockService: BlockService,
    private blockFileService: BlockFileService,
  ) {}

  isMediaBlock(type: string) {
    if (type === 'image' || type === 'video') {
      return true;
    }
  }

  async create(createPostApiDto: CreatePostApiDto): Promise<{ post: Post; blocks: Block[] }> {
    const { userEmail, title, blocks } = createPostApiDto;
    userEmail;
    const userUuid = 'test';

    const createdPost = await this.postService.create({ title, userUuid });

    const createdBlocks = blocks.map(async (block, index) => {
      const createdBlock = await this.blockService.create(createdPost.uuid, {
        content: block.content,
        order: index,
        type: block.type,
      });

      // 별도의 타입
      // 프론트에 안보내도 되는 값들 필터링해서 리턴해주기
      const blockWithFile = { ...createdBlock, blockFile: [] };

      if (this.isMediaBlock(block.type)) {
        blockWithFile.blockFile = block.blockFiles.map(async (blockFile) => {
          const createdBlockFile = await this.blockFileService.create(createdBlock.uuid, blockFile);
          return createdBlockFile;
        });
      }

      return blockWithFile;
    });

    return { post: createdPost, blocks: createdBlocks };
  }
}
