import { Controller, Get, Post, Body, Patch, Param, Delete, UseInterceptors, UploadedFile } from '@nestjs/common';
import { FileService } from './file.service';
import { UpdateFileDto } from './dto/update-file.dto';
import { FileInterceptor } from '@nestjs/platform-express';
import {
  ApiConsumes,
  ApiCreatedResponse,
  ApiExcludeEndpoint,
  ApiNotFoundResponse,
  ApiOkResponse,
  ApiOperation,
  ApiTags,
} from '@nestjs/swagger';
import { FileDto } from './dto/file.dto';

@ApiTags('files')
@Controller('files')
export class FileController {
  constructor(private readonly fileService: FileService) {}

  @Post()
  @UseInterceptors(FileInterceptor('file'))
  @ApiOperation({
    summary: '파일 업로드 API',
    description: '파일을 서버에 업로드하고, 파일의 정보를 받는다.',
  })
  @ApiConsumes('multipart/form-data')
  @ApiCreatedResponse({ type: FileDto })
  create(@UploadedFile() file: Express.Multer.File) {
    return this.fileService.create({ file });
  }

  @Get()
  @ApiOperation({
    summary: '파일 조회 API',
    description: '업로드한 파일의 정보를 받는다.',
  })
  @ApiOkResponse({ type: [FileDto] })
  findAll() {
    return this.fileService.findMany();
  }

  @Get(':uuid')
  @ApiOperation({
    summary: '파일 개별 조회 API',
    description: '업로드한 파일의 정보를 받는다.',
  })
  @ApiOkResponse({ type: FileDto })
  @ApiNotFoundResponse({
    description: '해당 파일 정보를 찾을 수 없습니다.',
  })
  findOne(@Param('uuid') uuid: string) {
    return this.fileService.findOne(uuid);
  }

  @ApiExcludeEndpoint()
  @Patch(':uuid')
  update(@Param('uuid') uuid: string, @Body() updateFileDto: UpdateFileDto) {
    return this.fileService.update(+uuid, updateFileDto);
  }

  @ApiExcludeEndpoint()
  @Delete(':uuid')
  remove(@Param('uuid') uuid: string) {
    return this.fileService.remove(+uuid);
  }
}
