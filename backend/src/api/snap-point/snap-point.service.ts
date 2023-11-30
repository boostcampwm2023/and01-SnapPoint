import { Injectable } from '@nestjs/common';

import { FileService } from '@/domain/file/file.service';
import { BlockService } from '@/domain/block/block.service';
import { BlockDto } from '@/domain/block/dtos/block.dto';

import { FileDto } from '@/api/file-api/dto/file.dto';
import { FindSnapPointDto } from '@/api/snap-point/dtos/find-snap-point.dto';

@Injectable()
export class SnapPointService {
  constructor(
    private readonly blockService: BlockService,
    private readonly fileService: FileService,
  ) {}

  async findSnapPoint(findAreaBlockDto: FindSnapPointDto): Promise<BlockDto[]> {
    // TODO: 제대로 된 영역인지 평가한다. (면적, 서비스 범위?)
    const blocks = await this.blockService.findBlocksWithCoordsByArea(findAreaBlockDto);

    const blockPromises = blocks.map(async (block) => {
      const files = await this.fileService.findFiles({ where: { source: 'block', sourceUuid: block.uuid } });
      const fileDtos = files.map((file) => FileDto.of(file));
      return BlockDto.of(block, fileDtos);
    });

    return Promise.all(blockPromises);
  }
}
