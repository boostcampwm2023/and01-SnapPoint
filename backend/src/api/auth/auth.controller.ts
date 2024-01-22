import { Controller, Post, Body, Res, Get } from '@nestjs/common';
import { AuthService } from './auth.service';
import { CreateAuthDto } from './dto/create-auth.dto';
import { LoginAuthDto } from './dto/login-auth.dto';
import { Response } from 'express';
import { ApiNotFoundResponse, ApiOkResponse, ApiOperation, ApiTags } from '@nestjs/swagger';
import { NoAuth } from '@/common/decorator/no-auth.decorator';
import { Cookies } from '@/common/decorator/cookie.decorator';

@ApiTags('auth')
@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('sign-up')
  @NoAuth()
  @ApiOperation({
    summary: '새로운 유저를 생성하는 API',
    description: '생성한 유저의 정보를 반환한다.',
  })
  @ApiOkResponse({ description: '성공적으로 유저 생성이 완료되었습니다.' })
  @ApiNotFoundResponse({ description: '해당 유저를 생성할 수 없습니다.' })
  create(@Body() createAuthDto: CreateAuthDto) {
    return this.authService.signup(createAuthDto);
  }

  @Post('sign-in')
  @NoAuth()
  @ApiOperation({
    summary: '로그인 API',
    description: '엑세스, 리프레시 토큰을 반환한다.',
  })
  @ApiOkResponse({ description: '성공적으로 유저 로그인이 완료되었습니다.' })
  @ApiNotFoundResponse({ description: '해당 유저 로그인을 할 수 없습니다.' })
  async login(@Body() loginAuthDto: LoginAuthDto, @Res({ passthrough: true }) res: Response) {
    const loginDto = await this.authService.validateUser(loginAuthDto);

    res.setHeader('Authorization', 'Bearer ' + [loginDto.accessToken, loginDto.refreshToken]);
    res.cookie('access_token', loginDto.accessToken, {
      httpOnly: true,
    });
    res.cookie('refresh_token', loginDto.refreshToken, {
      httpOnly: true,
    });

    return loginDto;
  }

  @Get('sign-out')
  @ApiOperation({
    summary: '로그아웃 API',
    description: '로그아웃에 성공하면 201번을 반환한다.',
  })
  @ApiOkResponse({ description: '성공적으로 로그아웃이 완료되었습니다.' })
  async signOut(@Cookies('refresh_token') refreshToken: string, @Res({ passthrough: true }) res: Response) {
    await this.authService.signOut(refreshToken);
    res.cookie('access_token', '', {
      httpOnly: true,
      maxAge: 0,
    });
    res.cookie('refresh_token', '', {
      httpOnly: true,
      maxAge: 0,
    });
  }
}
