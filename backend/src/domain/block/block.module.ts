import { Module } from '@nestjs/common';
import { BlockService } from '@/domain/block/block.service';
import { BlockRepository } from '@/domain/block/block.repository';

@Module({
  providers: [BlockRepository, BlockService],
  exports: [BlockService],
})
export class BlockModule {}
