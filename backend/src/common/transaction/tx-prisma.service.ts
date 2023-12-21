import { Inject, Injectable, OnModuleInit } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import { TransactionStorage } from './transaction.storage';

@Injectable()
export class TxPrismaService extends PrismaClient implements OnModuleInit {
  constructor(@Inject('TRANSACTION_STORAGE') private readonly asyncLocalStorage: TransactionStorage) {
    super();
  }

  async onModuleInit() {
    await this.$connect();
    // overwrite prisma
    Object.assign(this, this.wrapPrisma());
  }

  /**
   * Extends prisma to use the transactional client from AsyncLocalStorage.
   */
  wrapPrisma() {
    const { asyncLocalStorage } = this;

    return this.$extends({
      query: {
        $allOperations: async ({ args, model, operation, query }) => {
          const store = asyncLocalStorage.getStore();

          if (!store) {
            return query(args);
          }

          if (!model) {
            return store.txClient[operation](args);
          }
          return store.txClient[model][operation](args);
        },
      },
    });
  }
}
