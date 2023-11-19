import { Body, Controller, Get, Param, Post, UsePipes } from '@nestjs/common';
import { PostApiService } from './post-api.service';
import { CreatePostApiDto } from './dtos/create-post-api.dto';
import { BlockTypeAndFilesValidationPipe } from './pipes/block-type-files-validation.pipe';

@Controller('posts')
export class PostApiController {
  constructor(private postApiService: PostApiService) {}

  @Get('/')
  posts() {
    return this.postApiService.posts();
  }

  @Get('/:uuid')
  post(@Param('uuid') uuid: string) {
    return this.postApiService.post({ uuid: uuid });
  }

  @Post('/')
  @UsePipes(BlockTypeAndFilesValidationPipe)
  create(@Body() createPostDto: CreatePostApiDto) {
    return this.postApiService.create(createPostDto);
  }
}
