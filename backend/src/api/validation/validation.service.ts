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
import { WritePostDto } from '../post-api/dtos/write-post.dto';

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

  async validateBlocks(blockDtos: WriteBlockDto[], blockFileDtos: WriteBlockFileDto[]) {
    if (blockDtos.length === 0) {
      throw new BadRequestException('Post must have at least one block.');
    }

    const sourceFileMap = new Map<string, WriteBlockFileDto[]>();
    blockFileDtos.forEach((blockFile) => {
      if (!sourceFileMap.has(blockFile.sourceUuid)) {
        sourceFileMap.set(blockFile.sourceUuid, []);
      }
      sourceFileMap.get(blockFile.sourceUuid).push(blockFile);
    });

    blockDtos.forEach((blockDto) => {
      const { uuid, type, latitude, longitude } = blockDto;

      const sourceFiles = sourceFileMap.get(uuid);

      if (type === 'text' && (latitude || longitude)) {
        throw new BadRequestException('Latitude and longitude should not be provided for media type');
      }

      if (type === 'media' && (!latitude || !longitude)) {
        throw new BadRequestException('Latitude and longitude should be provided for media type');
      }

      if (type === 'text' && sourceFiles) {
        throw new BadRequestException('File block must not have media file.');
      }

      if (type === 'media') {
        if (!sourceFiles || sourceFiles.length === 0) {
          throw new BadRequestException('Media Block must have at least one files.');
        }

        sourceFiles.forEach((sourceFile) => {
          if (sourceFile.sourceUuid !== uuid) {
            throw new ConflictException('The file has been attached with other resource.');
          }
        });
      }
    });
  }
}
