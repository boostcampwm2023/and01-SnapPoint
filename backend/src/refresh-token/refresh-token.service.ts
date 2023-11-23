import { Injectable, InternalServerErrorException } from '@nestjs/common';
import { CreateRefreshTokenDto } from './dto/create-refresh-token.dto';
import { PrismaProvider } from '@/prisma.service';
import { Prisma, RefreshToken, User } from '@prisma/client';
import { ConfigService } from '@nestjs/config';
import { JwtService } from '@nestjs/jwt';

@Injectable()
export class RefreshTokenService {
  constructor(
    readonly prisma: PrismaProvider,
    readonly jwtService: JwtService,
    readonly configService: ConfigService,
  ) {}

  async create(createRefreshTokenDto: CreateRefreshTokenDto): Promise<RefreshToken> {
    const createdRefreshToken = await this.prisma.get().refreshToken.create({
      data: {
        userUuid: createRefreshTokenDto.userUuid,
        token: createRefreshTokenDto.token,
        expiresAt: createRefreshTokenDto.expiresAt,
      },
    });

    return createdRefreshToken;
  }

  async update(createRefreshTokenDto: CreateRefreshTokenDto): Promise<RefreshToken> {
    const updatedRefreshToken = await this.prisma.get().refreshToken.update({
      where: {
        userUuid: createRefreshTokenDto.userUuid,
      },
      data: {
        token: createRefreshTokenDto.token,
        expiresAt: createRefreshTokenDto.expiresAt,
      },
    });

    return updatedRefreshToken;
  }

  async findRefreshTokenByUnique(where: Prisma.RefreshTokenWhereUniqueInput): Promise<RefreshToken | null> {
    const refreshToken = await this.prisma.get().refreshToken.findUnique({
      where,
    });

    return refreshToken;
  }

  async generateAccessToken(user: User): Promise<string> {
    const payload = {
      uuid: user.uuid,
      email: user.email,
    };
    return await this.jwtService.signAsync(payload);
  }

  async generateRefreshToken(user: User): Promise<string> {
    const payload = {
      uuid: user.uuid,
    };
    return await this.jwtService.signAsync(payload, {
      secret: this.configService.get<string>('JWT_REFRESH_SECRET'),
      expiresIn: this.configService.get<string>('JWT_REFRESH_EXPIRATION_TIME'),
    });
  }

  async getCurrentRefreshTokenExp(): Promise<Date> {
    const currentDate = new Date();

    const expireTime = this.configService.get<string>('JWT_REFRESH_EXPIRATION_TIME');

    if (!expireTime) {
      throw new InternalServerErrorException();
    }

    const currentRefreshTokenExp = new Date(currentDate.getTime() + parseInt(expireTime));
    return currentRefreshTokenExp;
  }
}
