import { PostService } from '@/domain/post/post.service';
import { FileService } from '@/domain/file/file.service';
import {
  BadRequestException,
  ConflictException,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { ValidateFileDto } from '@/api/validation/dtos/validate-file.dto';
import { ValidateBlockDto } from '@/api/validation/dtos/validate-block.dto';
import { FindNearbyPostDto } from '../post-api/dtos/find-nearby-post.dto';
import { ValidatePostDto } from './dtos/validate-post.dto';
import { UtilityService } from '@/common/utility/utility.service';
import { BlockService } from '@/domain/block/block.service';
import { Block } from '@prisma/client';

@Injectable()
export class ValidationService {
  constructor(
    private readonly postService: PostService,
    private readonly fileService: FileService,
    private readonly utils: UtilityService,
  ) {}

  /**
   * 조회할 영역이 적합한 영역인지 검사해 반환한다.
   * @param dto
   */
  validateLookupArea(dto: FindNearbyPostDto) {
    const { latitudeMin, latitudeMax, longitudeMin, longitudeMax } = dto;

    const dist = this.utils.calDistance(latitudeMin, longitudeMin, latitudeMax, longitudeMax);

    if (dist > 20.0) {
      throw new BadRequestException(`The lookup areas is too large`);
    }
  }

  async validateFiles(fileDtos: ValidateFileDto[], userUuid: string) {
    const fileWhereInputs = fileDtos.map((fileDto) => ({ uuid: fileDto.uuid }));
    const existFiles = await this.fileService.findFilesById(fileWhereInputs);

    const existFileUuidSet = new Set(existFiles.map((existFile) => existFile.uuid));

    fileDtos.forEach((fileDto) => {
      if (!existFileUuidSet.has(fileDto.uuid)) {
        throw new BadRequestException(`The file with uuid ${fileDto.uuid} is invalid or not exist anymore.`);
      }
    });

    existFiles.forEach((existFile) => {
      if (existFile.userUuid !== userUuid) {
        throw new ForbiddenException(
          `Could not access the file with uuid ${existFile.uuid}. please check your permission.`,
        );
      }

      if (existFile.sourceUuid) {
        throw new ConflictException(`The file with uuid ${existFile.uuid} is already attached with other resource.`);
      }
    });
  }

  async validateBlocks(blockDtos: ValidateBlockDto[], blockFileDtos: ValidateFileDto[]) {
    const sourceFileMap = new Map<string, AttachFileDto[]>();

    // 블록이 다른 포스트에 있지 않은지 판별한다.
    blockFileDtos.forEach((blockFile) => {
      const { sourceUuid } = blockFile;
      if (!sourceFileMap.has(sourceUuid)) {
        sourceFileMap.set(sourceUuid, []);
      }
      const fileDtos = sourceFileMap.get(sourceUuid);
      if (fileDtos) {
        fileDtos.push(blockFile);
      }
    });

    blockDtos.forEach((blockDto) => {
      const { uuid, type, latitude, longitude } = blockDto;
      const sourceFiles = sourceFileMap.get(uuid);

      if (type === 'text' && (latitude !== undefined || longitude !== undefined)) {
        throw new BadRequestException('Latitude and longitude should not be provided for text type');
      }

      if (type === 'media' && (latitude === undefined || longitude === undefined)) {
        throw new BadRequestException('Latitude and longitude should be provided for media type');
      }

      if (type === 'text' && sourceFiles) {
        throw new BadRequestException('File block must not have media file');
      }

      if (type === 'media' && (!sourceFiles || sourceFiles.length === 0)) {
        throw new BadRequestException('Media Block must have at least one files');
      }
    });
  }

  async validatePost(dto: ValidatePostDto) {
    const { uuid, userUuid } = dto;

    const post = await this.postService.findPost({ uuid });

    if (!post) {
      throw new NotFoundException(`Cloud not found post with UUID: ${uuid}`);
    }

    if (post.userUuid !== userUuid) {
      throw new ForbiddenException('Could not access this post. please check your permission.');
    }

    return post;
  }

  private validateBlock(blockDto: ValidateBlockDto, sourceFiles?: ValidateFileDto[]) {
    const { type, latitude, longitude } = blockDto;

    if (type === 'text' && (latitude !== undefined || longitude !== undefined)) {
      throw new BadRequestException('Latitude and longitude should not be provided for text type');
    }

    if (type === 'media' && (latitude === undefined || longitude === undefined)) {
      throw new BadRequestException('Latitude and longitude should be provided for media type');
    }

    if (type === 'text' && sourceFiles) {
      throw new BadRequestException('File block must not have media file');
    }

    if (type === 'media' && (!sourceFiles || sourceFiles.length === 0)) {
      throw new BadRequestException('Media Block must have at least one files');
    }
  }
}
