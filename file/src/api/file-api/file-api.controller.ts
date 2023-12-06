import {
  Controller,
  Post,
  UseInterceptors,
  UploadedFile,
  UseGuards,
  Req,
  Inject,
  ParseFilePipeBuilder,
  HttpStatus,
  UploadedFiles,
} from '@nestjs/common';

import {
  FileFieldsInterceptor,
  FileInterceptor,
} from '@nestjs/platform-express';
import { ApiBody, ApiConsumes, ApiOperation, ApiTags } from '@nestjs/swagger';
import { ClientProxy } from '@nestjs/microservices';
import { UploadService } from '@/upload/upload.service';
import { JwtAuthGuard } from '@/common/guards/jwt.guard';

@ApiTags('files')
@Controller('files')
export class FileApiController {
  constructor(
    private readonly uploadService: UploadService,
    @Inject('DATA_SERVICE') private readonly client: ClientProxy,
  ) {}

  @Post('/image')
  @UseInterceptors(FileInterceptor('file'))
  @ApiOperation({
    summary: '이미지 업로드 API',
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
  @UseGuards(JwtAuthGuard)
  async uploadImage(
    @UploadedFile(
      new ParseFilePipeBuilder()
        .addMaxSizeValidator({
          maxSize: 1024 * 1024 * 2,
        })
        .addFileTypeValidator({ fileType: 'image/webp' })
        .build({
          errorHttpStatusCode: HttpStatus.UNPROCESSABLE_ENTITY,
        }),
    )
    file: Express.Multer.File,
    @Req() req: any,
  ) {
    const uploadedFileDto = await this.uploadService.uploadFile(file);

    this.client.emit(
      { cmd: 'create_image_data' },
      { ...uploadedFileDto, userUuid: req.user.uuid },
    );
    return uploadedFileDto;
  }

  @Post('/video')
  @ApiOperation({
    summary: '동영상 업로드 API',
    description: '파일을 서버에 업로드하고, 파일의 정보를 받는다.',
  })
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        image: {
          type: 'string',
          format: 'binary',
          description: '업로드할 이미지의 바이너리 데이터입니다.',
        },
        video: {
          type: 'string',
          format: 'binary',
          description: '업로드할 영상의 바이너리 데이터입니다.',
        },
      },
    },
  })
  @UseGuards(JwtAuthGuard)
  @UseInterceptors(
    FileFieldsInterceptor(
      [
        { name: 'image', maxCount: 1 },
        { name: 'video', maxCount: 1 },
      ],
      {
        limits: {
          fileSize: 1024 * 1024 * 5,
        },
      },
    ),
  )
  async uploadVideo(
    @UploadedFiles()
    files: {
      image: Express.Multer.File[];
      video: Express.Multer.File[];
    },
  ) {
    return await this.uploadService.uploadFileField(files);
  }
}
