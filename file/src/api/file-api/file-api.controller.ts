import {
  Controller,
  Post,
  UseInterceptors,
  UploadedFile,
  UseGuards,
  Req,
  Inject,
} from '@nestjs/common';

import { FileInterceptor } from '@nestjs/platform-express';
import { ApiBody, ApiConsumes, ApiOperation, ApiTags } from '@nestjs/swagger';
import { ClientProxy } from '@nestjs/microservices';
import { UploadService } from '@/upload/upload.service';
import { JwtAuthGuard } from '@/common/guards/jwt.guard';

@ApiTags('files')
@Controller('files')
export class FileApiController {
  constructor(
    private readonly uploadService: UploadService,
    @Inject('MAIN_SERVICE') private readonly fileClient: ClientProxy,
  ) {}

  @Post('/')
  @UseInterceptors(FileInterceptor('file'))
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
  @UseGuards(JwtAuthGuard)
  async upload(@UploadedFile() file: Express.Multer.File, @Req() req: any) {
    const uploadedFileDto = await this.uploadService.uploadFile(file);

    this.fileClient.emit(
      { cmd: 'upload_file' },
      { ...uploadedFileDto, userUuid: req.user.uuid },
    );

    return uploadedFileDto;
  }
}
