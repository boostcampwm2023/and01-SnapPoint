import { FileService } from '@/domain/file/file.service';
import { BadRequestException, ConflictException, ForbiddenException, Injectable } from '@nestjs/common';
import { WriteBlockFileDto } from '@/api/post-api/dtos/write-block-files.dto';
import { ValidateFileDto } from '@/api/validation/dtos/validate-file.dto';
import { ValidateBlockDto } from '@/api/validation/dtos/validate-block.dto';

@Injectable()
export class ValidationService {
  constructor(private readonly fileService: FileService) {}

  async validateFiles(fileDtos: ValidateFileDto[], userUuid: string) {
    const fileWhereInputs = fileDtos.map((fileDto) => ({ uuid: fileDto.uuid }));
    const existFiles = await this.fileService.findFiles({ where: { OR: fileWhereInputs } });

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
    const sourceFileMap = new Map<string, WriteBlockFileDto[]>();

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

      if (type === 'text' && (latitude || longitude)) {
        throw new BadRequestException('Latitude and longitude should not be provided for text type');
      }

      if (type === 'media' && (!latitude || !longitude)) {
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
}
