import { inspect } from 'util';
import { Body, Controller, Get, Logger, Param, Post, Put, Req, UsePipes } from '@nestjs/common';
import { PostApiService } from './post-api.service';
import { PostRequestDecompositionPipe } from './pipes/post-request-decompositon.pipe';
import { ComposedPostDto } from './dtos/composed-post.dto';
import { validationPipe } from '@/common/pipes/validation.pipe';
import { NoAuth } from '@/common/decorator/no-auth.decorator';
import { PostDto } from '@/domain/post/dtos/post.dto';
import { ApiOperation, ApiOkResponse, ApiNotFoundResponse, ApiCreatedResponse, ApiParam } from '@nestjs/swagger';

@Controller('posts')
export class PostApiController {
  constructor(private readonly postApiService: PostApiService) {}

  @NoAuth()
  @Get('/:uuid')
  @ApiParam({ name: 'uuid', required: true })
  @ApiOperation({
    summary: '게시글을 조회하는 API',
    description: '게시글과 연관된 블록 정보를 반환한다.',
  })
  @ApiOkResponse({ description: '성공적으로 게시글 조회가 완료되었습니다.' })
  @ApiNotFoundResponse({ description: '해당 UUID에 맞는 게시글을 찾을 수 없습니다.' })
  readPost(@Param('uuid') uuid: string) {
    return this.postApiService.readPost(uuid);
  }

  readPosts() {}

  @Post('/publish')
  @NoAuth()
  @UsePipes(PostRequestDecompositionPipe, validationPipe)
  @ApiOperation({
    summary: '게시글을 작성하는 API',
    description: '작성 중인 게시글 블록을 받아 게시글을 생성하고, 임시 저장한다.',
  })
  @ApiCreatedResponse({
    description: '게시글이 성공적으로 저장되었습니다.',
    type: PostDto,
  })
  @ApiNotFoundResponse({ description: '업로드한 파일 정보를 찾을 수 없습니다.' })
  writePost(@Body() postDto: ComposedPostDto, @Req() request: any) {
    request;
    // const { uuid: userUuid } = request.user;
    return this.postApiService.writePost(postDto, 'c6a7c590-6239-4d12-ad8f-c8065db60d6a');
  }

  @Put('/:uuid')
  @ApiOperation({ summary: '게시글을 수정하는 API', description: 'UUID에 맞는 게시글의 내용을 업데이트한다.' })
  @ApiParam({ name: 'uuid', required: true })
  @ApiOkResponse({
    description: '작성한 게시글의 내용 및 블록 정보를 업데이트한다.',
    type: PostDto,
  })
  modifyPost(
    @Param('uuid') uuid: string,
    @Body(PostRequestDecompositionPipe, validationPipe) postDto: ComposedPostDto,
    @Req() request: any,
  ) {
    const { uuid: userUuid } = request.user;
    return this.postApiService.modifyPost(uuid, userUuid, postDto);
  }

  deletePost() {}
}
