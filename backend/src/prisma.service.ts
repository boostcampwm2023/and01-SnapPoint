import { Injectable, OnModuleInit } from '@nestjs/common';
import { Prisma, PrismaClient } from '@prisma/client';
import { AsyncLocalStorage } from 'async_hooks';

export class PrismaService extends PrismaClient implements OnModuleInit {
  async onModuleInit() {
    await this.$connect();
  }
}

@Injectable()
export class PrismaProvider {
  private readonly prismaClient: PrismaService;
  private asyncLocalStorage = new AsyncLocalStorage<{ transactionClient?: Prisma.TransactionClient }>();

  constructor() {
    this.prismaClient = new PrismaService();
  }

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
