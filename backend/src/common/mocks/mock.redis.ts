import Redis from 'ioredis';
import { DeepMockProxy, mockDeep } from 'jest-mock-extended';

const redisClient = mockDeep<Redis>();

export type MockRedisService = {
  getClient: () => DeepMockProxy<Redis>;
};

export const mockRedisService: MockRedisService = {
  getClient: () => redisClient,
};
