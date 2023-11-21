import { Body, Controller, Param, Post, Put } from '@nestjs/common';
import { PostApiService } from './post-api.service';
import { SavePostApiDto } from './dtos/save-post-api.dto';
import { CreatePostApiDto } from './dtos/create-post-api.dto';

@Controller('posts')
export class PostApiController {
  constructor(private readonly postApiService: PostApiService) {}

  @Post('/')
  create(@Body() createPostDto: CreatePostApiDto) {
    return this.postApiService.create(createPostDto);
  }

  @Put('/:uuid')
  save(@Param('uuid') uuid: string, @Body() savePostDto: SavePostApiDto) {
    return this.postApiService.save(uuid, savePostDto);
  }

  @Put('/:uuid/publish')
  publish(@Param('uuid') uuid: string, @Body() savePostDto: SavePostApiDto) {
    return this.postApiService.publish(uuid, savePostDto);
  }
}
