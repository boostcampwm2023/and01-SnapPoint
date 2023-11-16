import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { PostApiService } from './post-api.service';
import { CreatePostApiDto } from './dtos/create-post-api.dto';

@Controller('posts')
export class PostApiController {
  constructor(private postApiService: PostApiService) {}

  // @Get('/')
  // getAll() {
  //   return this.postApiService.posts();
  // }

  // @Get('/:id')
  // get(@Param('id') uuid: string) {
  //   return this.postApiService.post({ uuid });
  // }

  @Post('/')
  create(@Body() createPostDto: CreatePostApiDto) {
    return this.postApiService.create(createPostDto);
  }
}
