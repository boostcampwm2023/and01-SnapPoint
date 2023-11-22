import { Body, Controller, Get, Param, Post, Put } from '@nestjs/common';
import { PostApiService } from './post-api.service';
import { CreatePostApiDto } from './dtos/create-post-api.dto';
import {
  ApiConflictResponse,
  ApiCreatedResponse,
  ApiNotFoundResponse,
  ApiOkResponse,
  ApiOperation,
  ApiParam,
  ApiTags,
} from '@nestjs/swagger';
import { PostDto } from '@/post/dtos/post.dto';

@ApiTags('posts')
@Controller('posts')
export class PostApiController {
  constructor(private readonly postApiService: PostApiService) {}

  @Get('/:uuid')
  @ApiOperation({
    summary: '게시글을 조회하는 API',
    description: '게시글과 연관된 블록 정보를 반환한다.',
  })
  @ApiOkResponse({ description: '성공적으로 게시글 조회가 완료되었습니다.' })
  @ApiNotFoundResponse({ description: '해당 UUID에 맞는 게시글을 찾을 수 없습니다.' })
  findOne(@Param('uuid') uuid: string) {
    return this.postApiService.readPost(uuid);
  }

  @Post('/')
  @ApiOperation({
    summary: '게시글을 생성하는 API',
    description: '작성 중인 게시글 블록을 받아 게시글을 생성하고, 임시 저장한다.',
  })
  @ApiCreatedResponse({
    description: '게시글이 성공적으로 저장되었습니다.',
    type: PostDto,
  })
  @ApiNotFoundResponse({ description: '업로드한 파일 정보를 찾을 수 없습니다.' })
  create(@Body() createPostDto: CreatePostApiDto) {
    return this.postApiService.write(createPostDto);
  }

  @Post('/publish')
  @ApiOperation({
    summary: '게시글을 생성 및 즉시 발행하는 API',
    description: '작성 중인 게시글 블록을 받아 게시글을 생성하고 게시글을 발행한다.',
  })
  @ApiCreatedResponse({
    description: '게시글이 성공적으로 등록 및 발행되었습니다.',
    type: PostDto,
  })
  @ApiNotFoundResponse({ description: '업로드한 파일 정보를 찾을 수 없습니다.' })
  createAndPublish(@Body() createPostDto: CreatePostApiDto) {
    return this.postApiService.writeAndPublish(createPostDto);
  }

  @Put('/:uuid')
  @ApiOperation({ summary: '게시글을 수정하는 API', description: 'UUID에 맞는 게시글의 내용을 업데이트한다.' })
  @ApiParam({ name: 'uuid', required: true })
  @ApiOkResponse({
    description: '임시로 저장된 게시글의 내용 및 블록 정보를 업데이트한다.',
    type: PostDto,
  })
  @ApiNotFoundResponse({ description: '지정한 게시글을 찾을 수 없습니다.' })
  @ApiNotFoundResponse({ description: '지정한 블록을 찾을 수 없습니다.' })
  @ApiNotFoundResponse({ description: '업로드한 파일 정보를 찾을 수 없습니다.' })
  save(@Param('uuid') uuid: string, @Body() savePostDto: CreatePostApiDto) {
    return this.postApiService.save(uuid, savePostDto);
  }

  @Put('/:uuid/publish')
  @ApiOperation({
    summary: '임시 저장된 게시글을 발행하는 API',
    description: '임시 저장된 게시글을 한번 더 저장하고, 발행 상태로 만든다.',
  })
  @ApiNotFoundResponse({ description: '업로드한 파일 정보를 찾을 수 없습니다.' })
  @ApiNotFoundResponse({ description: '지정한 블록을 찾을 수 없습니다.' })
  @ApiConflictResponse({ description: '이미 게시된 게시물입니다.' })
  saveAndPublish(@Param('uuid') uuid: string, @Body() savePostDto: CreatePostApiDto) {
    return this.postApiService.publish(uuid, savePostDto);
  }
}
