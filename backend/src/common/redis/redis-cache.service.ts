import { RedisService } from '@liaoliaots/nestjs-redis';
import { Injectable } from '@nestjs/common';
import { Redis } from 'ioredis';

@Injectable()
export class RedisCacheService {
  private readonly redisClient: Redis;

  constructor(private readonly redisService: RedisService) {
    this.redisClient = redisService.getClient();
  }

  async set<T>(key: string, value: T, converter: (value: T) => string): Promise<string> {
    return await this.redisClient.set(key, converter(value));
  }

  async get<T>(key: string, converter: (result: string) => T, finder: (key: string) => Promise<string>): Promise<T> {
    let result = await this.redisClient.get(key);
    if (result == null || result == undefined) {
      result = await finder(key);
      await this.redisClient.set(key, result);
    }
    return converter(result);
  }

  async del(key: string): Promise<number> {
    return await this.redisClient.del(key);
  }

  /**
   *
   * @param key Redis Key
   * @param value 실제 넣으려는 값 배열로
   * @param expiretime 만료 시간 초 단위
   * @param converter 가져온 값을 원하는 string 형식으로 변환하는 함수
   * @returns
   */
  async sadd<T>(key: string, value: T[], expiretime: number, converter: (value: T) => string): Promise<void> {
    const convertedList = value.map((v: T) => converter(v));

    if (convertedList.length <= 0) {
      return;
    }

    await this.redisClient.sadd(key, ...convertedList);
    await this.redisClient.expire(key, expiretime);
  }

  /**
   *
   * @param key Redis Key
   * @param converter 가져온 문자열 값을 원하는 형식으로 변환하는 함수
   * @param finder Redis에 값이 없을 경우 데이터를 찾아오는 비동기 함수
   * @returns
   */
  async smembers<T>(
    key: string,
    converter: (result: string) => T,
    finder?: (key: string) => Promise<T[]>,
  ): Promise<T[] | null> {
    const members = await this.redisClient.smembers(key);

    if (members.length <= 0 || !members) {
      if (!finder) {
        return null;
      }
      const finderMembers = await finder(key);

      if (finderMembers.length <= 0) {
        return finderMembers;
      }

      const stringMembers = finderMembers.map((value) => JSON.stringify(value));
      await this.redisClient.sadd(key, ...stringMembers);
      await this.redisClient.expire(key, 30);

      return finderMembers;
    }

    return members.map((value: string) => converter(value));
  }
}
