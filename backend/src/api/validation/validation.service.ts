import { FileService } from '@/domain/file/file.service';
import {
  BadRequestException,
  ConflictException,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { WriteBlockFileDto } from '../post-api/dtos/write-block-files.dto';
import { WriteBlockDto } from '../post-api/dtos/write-block.dto';
import { PostService } from '@/domain/post/post.service';
import { Post } from '@prisma/client';

@Injectable()
export class ValidationService {
  constructor(
    private readonly fileService: FileService,
    private readonly postService: PostService,
  ) {}

  async validateAttachFiles(fileDtos: WriteBlockFileDto[], userUuid: string) {
    const fileWhereInputs = fileDtos.map((fileDto) => ({ uuid: fileDto.uuid }));
    const existFiles = await this.fileService.findFiles({ where: { OR: fileWhereInputs } });

    if (fileDtos.length !== existFiles.length) {
      throw new BadRequestException('The file with uuid is invalid or not exist anymore.');
    }

    existFiles.forEach((existFile) => {
      if (existFile.userUuid !== userUuid) {
        throw new ForbiddenException('Could not access this file. please check your permission.');
      }

      if (existFile.sourceUuid) {
        throw new ConflictException('The file has been already attached with other resource.');
      }

      if (existFile.isDeleted) {
        throw new BadRequestException('The file with uuid is invalid or not exist anymore.');
      }
    });
  }

  async validateReadPost(uuid: string): Promise<Post> {
    const post = await this.postService.findPost({ uuid });

    if (!post || post.isDeleted) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }

    return post;
  }

  async validateModifyPost(uuid: string, userUuid: string): Promise<Post> {
    const post = await this.postService.findPost({ uuid });

    if (!post || post.isDeleted) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }

    if (post.userUuid !== userUuid) {
      throw new ForbiddenException('Could not access this post. please check your permission.');
    }

    return post;
  }

  async validateBlocks(blockDtos: WriteBlockDto[]) {
    if (blockDtos.length === 0) {
      throw new BadRequestException('Post must have at least one block.');
    }

    blockDtos.forEach((blockDto) => {
      const { type, latitude, longitude } = blockDto;

      if (type === 'text' && (latitude || longitude)) {
        throw new BadRequestException('Latitude and longitude should not be provided for media type');
      }

      if (type === 'media' && (!latitude || !longitude)) {
        throw new BadRequestException('Latitude and longitude should be provided for media type');
      }
    });
  }
}
