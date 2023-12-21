import { DynamicModule, Module } from '@nestjs/common';
import { TransactionManager } from './transaction.manager';
import { DiscoveryModule } from '@nestjs/core';
import { TxPrismaService } from './tx-prisma.service';
import { transactionStorage } from './transaction.storage';

type TransactionModuleOption = {
  isGlobal: boolean;
};

@Module({})
export class TransactionModule {
  static forRoot(options: TransactionModuleOption = { isGlobal: true }): DynamicModule {
    const { isGlobal } = options;

    const providers = [
      TransactionManager,
      TxPrismaService,
      {
        provide: 'TRANSACTION_STORAGE',
        useValue: transactionStorage,
      },
    ];

    return {
      module: TransactionModule,
      imports: [DiscoveryModule],
      global: isGlobal ? true : false,
      providers,
      exports: providers,
    };
  }
}
