import { RedisService } from '@liaoliaots/nestjs-redis';
import { Injectable } from '@nestjs/common';
import { Redis } from 'ioredis';

@Injectable()
export class RedisCacheService {
  private readonly redisClient: Redis;

  constructor(private readonly redisService: RedisService) {
    this.redisClient = redisService.getClient();
  }

  async set(key: string, value: string): Promise<string> {
    return await this.redisClient.set(key, value);
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

  async sadd(key: string, value: string[], expiretime: number): Promise<void> {
    await this.redisClient.sadd(key, ...value);
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
    finder: (key: string) => Promise<T[]>,
  ): Promise<T[]> {
    const members = await this.redisClient.smembers(key);

    if (members.length <= 0 || !members) {
      const finderMembers = await finder(key);
      const stringMembers = finderMembers.map((value) => JSON.stringify(value));
      await this.sadd(key, stringMembers, 30);
      return finderMembers;
    }

    return members.map((value: string) => converter(value));
  }

  // async hset(key: string, field: string, value: string): Promise<number> {
  //   return await this.redisClient.hset(key, field, value);
  // }

  // async hget<T>(
  //   key: string,
  //   field: string,
  //   converter: (result: string) => T,
  //   finder: (key: string, field: string) => Promise<string>,
  // ): Promise<T> {
  //   let result: string | null = await this.redisClient.hget(key, field);

  //   if (result == null || result === undefined) {
  //     result = await finder(key, field);
  //     await this.redisClient.hset(key, field, result);
  //   }

  //   return converter(result);
  // }

  // async hmset(key: string, object: string[]): Promise<string> {
  //   return await this.redisClient.hmset(key, object);
  // }

  // async hmget<T>(
  //   key: string,
  //   field: string,
  //   converter: (result: string) => T,
  //   finder: (key: string, field: string) => Promise<string>,
  // ): Promise<T[]> {
  //   const result = await this.redisClient.hmget(key, field);
  //   const convertedList = await Promise.all(
  //     result.map(async (value: string) => {
  //       let returnValue = value;
  //       if (value == null || value === undefined) {
  //         returnValue = await finder(key, field);
  //         await this.redisClient.hset(key, field, returnValue);
  //       }
  //       return converter(returnValue);
  //     }),
  //   );
  //   return convertedList;
  // }
}
