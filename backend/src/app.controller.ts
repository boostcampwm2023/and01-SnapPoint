import { Body, Controller, Get, Post } from '@nestjs/common';
import { AppService } from './app.service';
import { User as UserModel } from '@prisma/client';

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get()
  getHello(): string {
    return this.appService.getHello();
  }

  @Get('/users')
  async getUsers(): Promise<UserModel[]> {
    return this.appService.getUsers();
  }

  @Post('/users')
  async createUser(@Body() userData: { email: string; password: string; nickname: string }): Promise<UserModel> {
    return this.appService.createUser(userData);
  }
}
