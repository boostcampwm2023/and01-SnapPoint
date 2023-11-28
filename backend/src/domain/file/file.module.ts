import { Module } from '@nestjs/common';
import { FileController } from './file.controller';
import { BucketService } from '@/common/bucket.service';
import { PrismaProvider } from '@/common/prisma/prisma.provider';
import { FileService } from './file.service';
import { PrismaModule } from '@/common/prisma/prisma.module';
import { PrismaService } from '@/common/prisma/prisma.service';

@Module({
  imports: [PrismaModule],
  controllers: [FileController],
  providers: [BucketService, PrismaService, PrismaProvider, FileService],
})
export class FileModule {}
