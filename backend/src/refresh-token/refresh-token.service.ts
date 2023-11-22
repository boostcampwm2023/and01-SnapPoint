import { Injectable } from '@nestjs/common';
import { CreateRefreshTokenDto } from './dto/create-refresh-token.dto';
import { PrismaProvider } from '@/prisma.service';
import { Prisma, RefreshToken } from '@prisma/client';

@Injectable()
export class RefreshTokenService {
  constructor(readonly prisma: PrismaProvider) {}

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
}
