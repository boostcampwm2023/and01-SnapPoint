import { Injectable } from '@nestjs/common';
import { SaveRefreshTokenDto } from './dto/save-refresh-token.dto';
import { ConfigService } from '@nestjs/config';
import { JwtService } from '@nestjs/jwt';
import { DeleteRefreshTokenDto } from './dto/delete-refresh-token.dto';
import { RedisCacheService } from '@/common/redis/redis-cache.service';
import { FindRefreshTokenDto } from './dto/find-refresh-token.dto';
import { GenerateAccessTokenDto } from './dto/generate-access-token.dto';
import { GenerateRefreshTokenDto } from './dto/generate-refresh-token.dto';

@Injectable()
export class RefreshTokenService {
  constructor(
    readonly jwtService: JwtService,
    readonly configService: ConfigService,
    private readonly redisService: RedisCacheService,
  ) {}

  async save(createRefreshTokenDto: SaveRefreshTokenDto) {
    const { userUuid, token } = createRefreshTokenDto;

    return this.redisService.set(
      `token:${userUuid}`,
      token,
      this.configService.getOrThrow<number>('JWT_REFRESH_EXPIRATION_TIME'),
      (v) => v,
    );
  }

  async delete(deleteRefreshTokenDto: DeleteRefreshTokenDto) {
    const { userUuid } = deleteRefreshTokenDto;

    return this.redisService.del(`token:${userUuid}`);
  }

  async findRefreshToken(findRefreshTokenDto: FindRefreshTokenDto) {
    const { userUuid } = findRefreshTokenDto;

    return this.redisService.get(`token:${userUuid}`, async (v) => v);
  }

  async generateAccessToken(generateAccessTokenDto: GenerateAccessTokenDto): Promise<string> {
    const { uuid, email, nickname } = generateAccessTokenDto;
    const payload = { uuid, email, nickname };

    return await this.jwtService.signAsync(payload, {
      secret: this.configService.getOrThrow<string>('JWT_ACCESS_SECRET'),
      expiresIn: this.configService.getOrThrow<string>('JWT_ACCESS_EXPIRATION_TIME'),
    });
  }

  async generateRefreshToken(generateRefreshTokenDto: GenerateRefreshTokenDto): Promise<string> {
    const { uuid } = generateRefreshTokenDto;
    const payload = { uuid };

    return await this.jwtService.signAsync(payload, {
      secret: this.configService.getOrThrow<string>('JWT_REFRESH_SECRET'),
      expiresIn: this.configService.getOrThrow<string>('JWT_REFRESH_EXPIRATION_TIME'),
    });
  }
}
