import { Controller, Get, Post, Body, Patch, Param, Delete, UseInterceptors, UploadedFile } from '@nestjs/common';
import { FileService } from './file.service';
import { UpdateFileDto } from './dto/update-file.dto';
import { FileInterceptor } from '@nestjs/platform-express';

@Controller('files')
export class FileController {
  constructor(private readonly fileService: FileService) {}

  @Post()
  @UseInterceptors(FileInterceptor('file'))
  create(@UploadedFile() file: Express.Multer.File) {
    return this.fileService.create({ file });
  }

  @Get()
  findAll() {
    return this.fileService.findMany();
  }

  @Get(':uuid')
  findOne(@Param('uuid') uuid: string) {
    return this.fileService.findOne(uuid);
  }

  @Patch(':uuid')
  update(@Param('uuid') uuid: string, @Body() updateFileDto: UpdateFileDto) {
    return this.fileService.update(+uuid, updateFileDto);
  }

  @Delete(':uuid')
  remove(@Param('uuid') uuid: string) {
    return this.fileService.remove(+uuid);
  }
}
