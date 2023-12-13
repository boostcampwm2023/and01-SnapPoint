import { Module } from '@nestjs/common';
import { BlockService } from '@/domain/block/block.service';
import { PrismaModule } from '@/common/prisma/prisma.module';
import { BlockRepository } from '@/domain/block/block.repository';

@Module({
  imports: [PrismaModule],
  providers: [BlockRepository, BlockService],
  exports: [BlockService],
})
export class BlockModule {}
