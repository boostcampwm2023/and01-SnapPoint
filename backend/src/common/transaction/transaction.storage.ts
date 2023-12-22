import { Prisma } from '@prisma/client';
import { AsyncLocalStorage } from 'async_hooks';

export type TransactionStorage = AsyncLocalStorage<{ txClient: Prisma.TransactionClient }>;
export const transactionStorage: TransactionStorage = new AsyncLocalStorage<{ txClient: Prisma.TransactionClient }>();
