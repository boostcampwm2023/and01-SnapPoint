import { Body, Controller, Delete, Get, Param, Post, Put, Query, Req } from '@nestjs/common';
import { PostApiService } from '@/api/post-api/post-api.service';
import { NoAuth } from '@/common/decorator/no-auth.decorator';
import { PostDto } from '@/domain/post/dtos/post.dto';
import {
  ApiOperation,
  ApiOkResponse,
  ApiNotFoundResponse,
  ApiCreatedResponse,
  ApiParam,
  ApiQuery,
} from '@nestjs/swagger';
import { FindNearbyPostQuery } from './dtos/find-nearby-post.query.dto';
import { WritePostDto } from './dtos/write-post.dto';
import { ReadPostQuery } from './dtos/read-post.query.dto';

@Controller('posts')
export class PostApiController {
  constructor(private readonly postApiService: PostApiService) {}

  @NoAuth()
  @Get('/:uuid')
  @ApiParam({ name: 'uuid', required: true })
  @ApiQuery({ name: 'detail', required: false })
  @ApiOperation({
    summary: '게시글을 조회하는 API',
    description: '게시글과 연관된 블록 정보를 반환한다.',
  })
  @ApiOkResponse({ description: '성공적으로 게시글 조회가 완료되었습니다.', type: PostDto })
  @ApiNotFoundResponse({ description: '해당 UUID에 맞는 게시글을 찾을 수 없습니다.' })
  readPost(@Param('uuid') uuid: string, @Query() query: ReadPostQuery) {
    const { detail } = query;
    return this.postApiService.findPost(uuid, detail);
  }

  @Get('/')
  @NoAuth()
  @ApiOperation({
    summary: '위치정보를 받아 근처 게시글을 조회하는 API',
    description: '근처에 있는 게시글과 연관된 블록 정보를 반환한다.',
  })
  @ApiOkResponse({ description: '성공적으로 게시글 조회가 완료되었습니다.', type: PostDto, isArray: true })
  readNearbyPosts(@Query() findNearbyPostQuery: FindNearbyPostQuery) {
    return this.postApiService.findNearbyPost(findNearbyPostQuery);
  }

  @Post('/publish')
  @ApiOperation({
    summary: '게시글을 작성하는 API',
    description: '작성 중인 게시글 블록을 받아 게시글을 생성하고, 임시 저장한다.',
  })
  @ApiCreatedResponse({
    description: '게시글이 성공적으로 저장되었습니다.',
    type: PostDto,
  })
  @ApiNotFoundResponse({ description: '업로드한 파일 정보를 찾을 수 없습니다.' })
  writePost(@Body() postDto: WritePostDto, @Req() request: any) {
    const { uuid: userUuid } = request.user;
    return this.postApiService.writePost(postDto, userUuid);
  }

  @Put('/:uuid')
  @ApiOperation({ summary: '게시글을 수정하는 API', description: 'UUID에 맞는 게시글의 내용을 업데이트한다.' })
  @ApiParam({ name: 'uuid', required: true })
  @ApiOkResponse({
    description: '작성한 게시글의 내용 및 블록 정보를 업데이트한다.',
    type: PostDto,
  })
  modifyPost(@Param('uuid') uuid: string, @Body() postDto: WritePostDto, @Req() request: any) {
    const { uuid: userUuid } = request.user;
    return this.postApiService.modifyPost(uuid, userUuid, postDto);
  }

  @Delete('/:uuid')
  @ApiOperation({ summary: '게시글을 삭제하는 API', description: 'UUID에 맞는 게시글과 연관된 블록, 파일을 삭제한다.' })
  @ApiParam({ name: 'uuid', required: true })
  @ApiOkResponse({
    description: '작성한 게시글의 내용 및 블록 정보를 업데이트한다.',
    type: PostDto,
  })
  deletePost(@Param('uuid') uuid: string, @Req() request: any) {
    const { uuid: userUuid } = request.user;
    return this.postApiService.deletePost(uuid, userUuid);
  }
}
