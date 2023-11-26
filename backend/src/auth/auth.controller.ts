import { Controller, Post, Body, Res } from '@nestjs/common';
import { AuthService } from './auth.service';
import { CreateAuthDto } from './dto/create-auth.dto';
import { LoginAuthDto } from './dto/login-auth.dto';
import { Response } from 'express';
import { RefreshTokenDto } from './dto/refresh-auth.dto';
import { UserService } from '@/user/user.service';
import { ApiOperation, ApiTags } from '@nestjs/swagger';

@ApiTags('auth')
@Controller('auth')
export class AuthController {
  constructor(
    private readonly authService: AuthService,
    private readonly userService: UserService,
  ) {}

  @Post()
  @ApiOperation({
    summary: '',
    description: '',
  })
  create(@Body() createAuthDto: CreateAuthDto) {
    return this.userService.create(createAuthDto);
  }

  @Post('login')
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
  async refresh(@Body() refreshTokenDto: RefreshTokenDto, @Res({ passthrough: true }) res: Response) {
    const refreshDto = await this.authService.refresh(refreshTokenDto);
    res.setHeader('Authorization', 'Bearer ' + refreshDto.accessToken);
    res.cookie('access_token', refreshDto.accessToken, {
      httpOnly: true,
    });
    return refreshDto;
  }
}
