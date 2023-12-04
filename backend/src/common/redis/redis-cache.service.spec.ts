import { Test, TestingModule } from '@nestjs/testing';
import { RedisCacheService } from './redis-cache.service';
import { RedisService } from '@liaoliaots/nestjs-redis';
import { DeepMockProxy } from 'jest-mock-extended';
import { MockRedisService, mockRedisService } from '@/common/mocks/mock.redis';

describe('RedisCacheService', () => {
  let cacheService: RedisCacheService;
  let redisService: DeepMockProxy<MockRedisService>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [RedisCacheService, RedisService],
    })
      .overrideProvider(RedisService)
      .useValue(mockRedisService)
      .compile();

    cacheService = module.get<RedisCacheService>(RedisCacheService);
    redisService = module.get(RedisService);
  });

  describe('set()', () => {
    it('캐싱된 값이 있는 경우 OK를 반환한다.', async () => {
      redisService.getClient().set.mockResolvedValueOnce('OK');

      expect(await cacheService.set('key', 'value')).toEqual('OK');
    });
  });

  describe('get()', () => {
    const mockKey = 'testKey';
    const mockValue = 'testValue';

    const converter = (result: string) => {
      return result;
    };

    const finder = (key: string) => {
      return new Promise<string>((resolve) => {
        resolve(key);
      });
    };

    it('캐싱된 값이 있는 경우 converter로 변환한 형태로 반환한다.', async () => {
      redisService.getClient().get.mockResolvedValueOnce(mockValue);

      const value = await cacheService.get<string>(mockKey, converter, finder);

      expect(value).toEqual(mockValue);
    });

    it('캐싱된 값이 없는 경우 finder에서 반환한 값을 converter로 변환한 형태로 반환한다.', async () => {
      redisService.getClient().get.mockResolvedValueOnce(null);

      const value = await cacheService.get<string>(mockKey, converter, finder);

      expect(value).toEqual(mockKey);
    });
  });

  describe('del()', () => {
    it('캐싱된 값이 있는 경우 삭제하고 1을 반환한다.', async () => {
      redisService.getClient().del.mockResolvedValueOnce(1);

      expect(await cacheService.del('key')).toEqual(1);
    });
  });
});
