import { BadRequestException, Injectable, PipeTransform } from '@nestjs/common';
import { CreatePostApiDto } from '../dtos/create-post-api.dto';

@Injectable()
export class BlockTypeAndFilesValidationPipe implements PipeTransform {
  transform(postData: CreatePostApiDto) {
    postData.blocks.forEach((block) => {
      if (block.type === 'text' && block.files) {
        throw new BadRequestException('Files should not be provided for text type');
      }

      if ((block.type === 'video' || block.type === 'image') && !block.files) {
        throw new BadRequestException('Files are required for video and image types');
      }
    });

    return postData;
  }
}
