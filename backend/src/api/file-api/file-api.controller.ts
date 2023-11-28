import { Controller, Get, Post, Param, UseInterceptors, UploadedFile, Req, Logger, UsePipes } from '@nestjs/common';

import { FileInterceptor } from '@nestjs/platform-express';
import {
  ApiBody,
  ApiConsumes,
  ApiCreatedResponse,
  ApiForbiddenResponse,
  ApiNotFoundResponse,
  ApiOkResponse,
  ApiOperation,
  ApiTags,
} from '@nestjs/swagger';
import { FileDto } from '../../api/file-api/dto/file.dto';
import { FileApiService } from './file-api.service';
import { validationPipe } from '@/common/pipes/validation.pipe';

@ApiTags('files')
@Controller('files')
export class FileApiController {
  constructor(private readonly fileApiService: FileApiService) {}

  @Post()
  @UseInterceptors(FileInterceptor('file'))
  @UsePipes(validationPipe)
  @ApiOperation({
    summary: '파일 업로드 API',
    description: '파일을 서버에 업로드하고, 파일의 정보를 받는다.',
  })
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        file: {
          type: 'string',
          format: 'binary',
          description: '업로드할 파일의 바이너리 데이터입니다.',
        },
      },
    },
  })
  @ApiCreatedResponse({ type: FileDto })
  upload(@UploadedFile() file: Express.Multer.File, @Req() request: any) {
    const userUuid = request.uuid;
    Logger.debug(userUuid);
    return this.fileApiService.uploadFile(file, userUuid);
  }

  @Get()
  @UsePipes(validationPipe)
  @ApiOperation({
    summary: '파일 조회 API',
    description: '자신이 업로드한 모든 파일 정보를 받는다.',
  })
  @ApiOkResponse({ type: [FileDto] })
  findFiles(@Req() request: any) {
    const { uuid: userUuid } = request;
    return this.fileApiService.findFiles(userUuid);
  }

  @Get(':uuid')
  @UsePipes(validationPipe)
  @ApiOperation({
    summary: '파일 개별 조회 API',
    description: '업로드한 파일의 개별 정보를 받는다.',
  })
  @ApiOkResponse({ type: FileDto })
  @ApiNotFoundResponse({ description: '해당 파일 정보를 찾을 수 없습니다.' })
  @ApiForbiddenResponse({ description: '해당 파일에 접근할 권한이 없습니다.' })
  findFile(@Param('uuid') uuid: string, @Req() request: any) {
    const { uuid: userUuid } = request;
    return this.fileApiService.findFile(uuid, userUuid);
  }
}
