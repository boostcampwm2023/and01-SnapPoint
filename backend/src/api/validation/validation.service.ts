import { FileService } from '@/domain/file/file.service';
import { BadRequestException, ConflictException, ForbiddenException, Injectable } from '@nestjs/common';
import { ValidateFileDto } from '@/api/validation/dtos/validate-file.dto';
import { ValidateBlockDto } from '@/api/validation/dtos/validate-block.dto';
import { AttachFileDto } from '../post-api/dtos/file/attach-file.dto';
import { FindNearbyPostDto } from '../post-api/dtos/find-nearby-post.dto';

@Injectable()
export class ValidationService {
  constructor(private readonly fileService: FileService) {}

  /**
   * 두 지점의 거리를 계산하고, KM 단위로 반환한다.
   * @param latMin 위도의 최솟값
   * @param lonMin 경도의 최솟값
   * @param latMax 위도의 최댓값
   * @param lonMax 경도의 최댓값
   * @returns 두 지점의 거리 (KM 단위)
   */
  private calDistance(latMin: number, lonMin: number, latMax: number, lonMax: number) {
    const R = 6371000; // 지구 반지름 (미터 단위)
    const rad = Math.PI / 180; // 도를 라디안으로 변환

    const x = (lonMax * rad - lonMin * rad) * Math.cos((latMin * rad + latMax * rad) / 2);
    const y = latMax * rad - latMin * rad;

    return (Math.sqrt(x * x + y * y) * R) / 1000;
  }

  /**
   * 조회할 영역이 적합한 영역(10km)인지 검사해 반환한다.
   * @param dto
   */
  validateLookupArea(dto: FindNearbyPostDto) {
    const { latitudeMin, latitudeMax, longitudeMin, longitudeMax } = dto;

    const dist = this.calDistance(latitudeMin, longitudeMin, latitudeMax, longitudeMax);

    if (dist > 10.0) {
      throw new BadRequestException(`The lookup areas is too large (10km Limit)`);
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
}
