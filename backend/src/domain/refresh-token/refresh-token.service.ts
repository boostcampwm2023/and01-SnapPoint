import { Inject, Injectable, InternalServerErrorException, NotFoundException } from '@nestjs/common';
import { CreateRefreshTokenDto } from './dto/create-refresh-token.dto';
import { Prisma, RefreshToken, User } from '@prisma/client';
import { ConfigService } from '@nestjs/config';
import { JwtService } from '@nestjs/jwt';
import { DeleteRefreshTokenDto } from './dto/delete-refresh-token.dto';
import { PRISMA_SERVICE, PrismaService } from '@/common/databases/prisma.service';

@Injectable()
export class RefreshTokenService {
  constructor(
    @Inject(PRISMA_SERVICE) private readonly prisma: PrismaService,
    readonly jwtService: JwtService,
    readonly configService: ConfigService,
  ) {}

  async create(createRefreshTokenDto: CreateRefreshTokenDto): Promise<RefreshToken> {
    return this.prisma.refreshToken.create({
      data: {
        userUuid: createRefreshTokenDto.userUuid,
        token: createRefreshTokenDto.token,
        expiresAt: createRefreshTokenDto.expiresAt,
      },
    });
  }

  async update(createRefreshTokenDto: CreateRefreshTokenDto): Promise<RefreshToken> {
    const refreshToken = await this.prisma.refreshToken.findUnique({
      where: {
        userUuid: createRefreshTokenDto.userUuid,
      },
    });

    if (!refreshToken) {
      throw new NotFoundException('해당 유저의 리프레시 토큰이 존재하지 않습니다.');
    }

    return this.prisma.refreshToken.update({
      where: {
        userUuid: createRefreshTokenDto.userUuid,
      },
      data: {
        token: createRefreshTokenDto.token,
        expiresAt: createRefreshTokenDto.expiresAt,
      },
    });
  }

  async delete(deleteRefreshTokenDto: DeleteRefreshTokenDto) {
    return this.prisma.refreshToken.delete({
      where: {
        userUuid: deleteRefreshTokenDto.userUuid,
      },
    });
  }

  async findRefreshTokenByUnique(where: Prisma.RefreshTokenWhereUniqueInput): Promise<RefreshToken | null> {
    return this.prisma.refreshToken.findUnique({
      where,
    });
  }

  async generateAccessToken(user: User): Promise<string> {
    const { uuid, email, nickname } = user;
    const payload = { uuid, email, nickname };

    return await this.jwtService.signAsync(payload, {
      secret: this.configService.getOrThrow<string>('JWT_ACCESS_SECRET'),
      expiresIn: this.configService.getOrThrow<string>('JWT_ACCESS_EXPIRATION_TIME'),
    });
  }

  async generateRefreshToken(user: User): Promise<string> {
    const { uuid, email, nickname } = user;
    const payload = { uuid, email, nickname };

    return await this.jwtService.signAsync(payload, {
      secret: this.configService.getOrThrow<string>('JWT_REFRESH_SECRET'),
      expiresIn: this.configService.getOrThrow<string>('JWT_REFRESH_EXPIRATION_TIME'),
    });
  }

  async getCurrentRefreshTokenExp(): Promise<Date> {
    const currentDate = new Date();

    const expireTime = this.configService.getOrThrow<string>('JWT_REFRESH_EXPIRATION_TIME');

    if (!expireTime) {
      throw new InternalServerErrorException();
    }

    const currentRefreshTokenExp = new Date(currentDate.getTime() + parseInt(expireTime));
    return currentRefreshTokenExp;
  }
}
