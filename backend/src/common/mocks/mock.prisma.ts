import { Prisma } from '@prisma/client';
import { DeepMockProxy, mockDeep } from 'jest-mock-extended';

const prismaClient = mockDeep<Prisma.TransactionClient>();

export type MockPrismaProvider = {
  get: () => DeepMockProxy<Prisma.TransactionClient>;
  beginTransaction: <T>(callback: () => Promise<T>) => Promise<T>;
};

export const mockPrismaProvider: MockPrismaProvider = {
  get: () => prismaClient,
  beginTransaction: async <T>(callback: () => Promise<T>) => callback(),
};
