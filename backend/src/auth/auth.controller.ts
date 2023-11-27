import { Controller, Post, Body, Res } from '@nestjs/common';
import { AuthService } from './auth.service';
import { CreateAuthDto } from './dto/create-auth.dto';
import { LoginAuthDto } from './dto/login-auth.dto';
import { Response } from 'express';
import { RefreshTokenDto } from './dto/refresh-auth.dto';
import { UserService } from '@/user/user.service';
import { ApiNotFoundResponse, ApiOkResponse, ApiOperation, ApiTags } from '@nestjs/swagger';

@ApiTags('auth')
@Controller('auth')
export class AuthController {
  constructor(
    private readonly authService: AuthService,
    private readonly userService: UserService,
  ) {}

  @Post()
  @ApiOperation({
    summary: '새로운 유저를 생성하는 API',
    description: '생성한 유저의 정보를 반환한다.',
  })
  @ApiOkResponse({ description: '성공적으로 유저 생성이 완료되었습니다.' })
  @ApiNotFoundResponse({ description: '해당 유저를 생성할 수 없습니다.' })
  create(@Body() createAuthDto: CreateAuthDto) {
    return this.userService.create(createAuthDto);
  }

  @Post('login')
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

  @Post('refresh')
  @ApiOperation({
    summary: '엑세스 토큰 재발급 API',
    description: '새로운 엑세스 토큰을 반환한다.',
  })
  @ApiOkResponse({ description: '성공적으로 엑세스 토큰 발급이 완료되었습니다.' })
  @ApiNotFoundResponse({ description: '해당 리프레시 토큰으로 새로운 엑세스 토큰을 발급할 수 없습니다.' })
  async refresh(@Body() refreshTokenDto: RefreshTokenDto, @Res({ passthrough: true }) res: Response) {
    const refreshDto = await this.authService.refresh(refreshTokenDto);
    res.setHeader('Authorization', 'Bearer ' + refreshDto.accessToken);
    res.cookie('access_token', refreshDto.accessToken, {
      httpOnly: true,
    });
    return refreshDto;
  }
}
