import { BadRequestException, PipeTransform } from '@nestjs/common';
import { plainToInstance } from 'class-transformer';
import { WritePostDto } from '../dtos/write-post.dto';
import { randomUUID } from 'crypto';
import { WriteBlockDto } from '../dtos/write-block.dto';
import { AttachFileDto } from '@/api/file-api/dto/attach-file.dto';
import { ComposedPostDto } from '../dtos/composed-post.dto';
import { WriteBlockFileDto } from '../dtos/write-block-files.dto';

export class PostRequestDecompositionPipe implements PipeTransform {
  transform(value: any): ComposedPostDto {
    if (!value) {
      throw new BadRequestException('No post data providied.');
    }

    const { title, blocks } = value;

    if (!blocks) {
      throw new BadRequestException('No block data providied.');
    }

    const postDto = plainToInstance(WritePostDto, { title });

    const fileDtos: AttachFileDto[] = [];

    const blockDtos = value.blocks.map((block, order) => {
      const { uuid, content, type, latitude, longitude, files } = block;
      const blockUuid = uuid ? uuid : randomUUID();

      if (files) {
        fileDtos.push(
          ...files.map((file) =>
            plainToInstance(WriteBlockFileDto, { uuid: file.uuid, source: 'block', sourceUuid: blockUuid }),
          ),
        );
      }

      return plainToInstance(WriteBlockDto, { uuid: blockUuid, content, type, latitude, longitude, order });
    });

    return plainToInstance(ComposedPostDto, {
      post: postDto,
      blocks: blockDtos,
      files: fileDtos,
    });
  }
}
