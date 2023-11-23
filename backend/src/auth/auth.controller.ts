import { Controller, Post, Body, Get, UseGuards, Res } from '@nestjs/common';
import { AuthService } from './auth.service';
import { CreateAuthDto } from './dto/create-auth.dto';
import { LoginAuthDto } from './dto/login-auth.dto';
import { JwtAuthGuard } from './guards/jwt-auth.guard';
import { Response } from 'express';
import { RefreshTokenDto } from './dto/refresh-auth.dto';
import { UserService } from '@/user/user.service';
@Controller('auth')
export class AuthController {
  constructor(
    private readonly authService: AuthService,
    private readonly userService: UserService,
  ) {}

  @Post()
  create(@Body() createAuthDto: CreateAuthDto) {
    return this.userService.create(createAuthDto);
  }

  @Post('login')
  async login(@Body() loginAuthDto: LoginAuthDto, @Res({ passthrough: true }) res: Response) {
    const { accessToken, refreshToken } = await this.authService.validateUser(loginAuthDto);

    res.setHeader('Authorization', 'Bearer ' + [accessToken, refreshToken]);
    res.cookie('access_token', accessToken, {
      httpOnly: true,
    });
    res.cookie('refresh_token', refreshToken, {
      httpOnly: true,
    });

    return {
      access_token: accessToken,
      refresh_token: refreshToken,
    };
  }

  @Post('refresh')
  async refresh(@Body() refreshTokenDto: RefreshTokenDto, @Res({ passthrough: true }) res: Response) {
    const { accessToken } = await this.authService.refresh(refreshTokenDto);
    res.setHeader('Authorization', 'Bearer ' + accessToken);
    res.cookie('access_token', accessToken, {
      httpOnly: true,
    });
    res.send({ access_token: accessToken });
  }

  @Get('test')
  @UseGuards(JwtAuthGuard)
  test() {
    return {
      test: 'testtest',
    };
  }
}
