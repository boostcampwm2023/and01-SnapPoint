import { Injectable } from '@nestjs/common';
import { Prisma } from '@prisma/client';
import { AsyncLocalStorage } from 'async_hooks';
import { PrismaService } from '../prisma.service';

@Injectable()
export class PrismaProvider {
  private asyncLocalStorage = new AsyncLocalStorage<{ transactionClient?: Prisma.TransactionClient }>();

  constructor(private readonly prismaClient: PrismaService) {}

  get() {
    const store = this.asyncLocalStorage.getStore();
    return store?.transactionClient || this.prismaClient;
  }

  async beginTransaction<T>(fn: () => Promise<T>): Promise<T> {
    return this.prismaClient.$transaction(async (transactionClient) => {
      return this.asyncLocalStorage.run({ transactionClient }, fn);
    });
  }
}
