import { Repository } from '../interfaces/repository.interface';
import { Injectable, OnModuleInit } from '@nestjs/common';
import { DiscoveryService, MetadataScanner, Reflector } from '@nestjs/core';
import { PrismaService } from '../prisma/prisma.service';
import { AsyncLocalStorage } from 'async_hooks';
import { TRANSACTIONAL_KEY } from './transacion.key';

@Injectable()
export class TransactionManager implements OnModuleInit {
  private readonly asyncLocalStorage = new AsyncLocalStorage<any>();

  constructor(
    private readonly discoverService: DiscoveryService,
    private readonly metadataScanner: MetadataScanner,
    private readonly reflector: Reflector,
    private readonly prisma: PrismaService,
  ) {}

  onModuleInit() {
    this.wrapDecorators();
    this.wrapRepository();
  }

  private getStaticInstances(): any[] {
    return this.discoverService
      .getProviders()
      .filter((wrapper) => wrapper.instance)
      .map((wrapper) => wrapper.instance);
  }

  private wrapMethods(method: any, instance: any) {
    const { prisma, asyncLocalStorage } = this;

    return async function (...args: any[]) {
      const result = await prisma.$transaction(async (txClient) =>
        asyncLocalStorage.run({ txClient }, async () => {
          return method.apply(instance, args);
        }),
      );
      return result;
    };
  }

  wrapDecorators() {
    const instances = this.getStaticInstances();

    instances.forEach((instance) => {
      const names = this.metadataScanner.getAllMethodNames(Object.getPrototypeOf(instance));

      names.forEach((name) => {
        const method = instance[name];
        if (!this.reflector.get(TRANSACTIONAL_KEY, method)) return;
        instance[name] = this.wrapMethods(method, instance);
      });
    });
  }

  wrapRepository() {
    const { asyncLocalStorage } = this;
    const instances = this.getStaticInstances();

    instances
      .filter((instance) => instance instanceof Repository)
      .forEach((instance) => {
        const { prisma: prevClient } = instance;

        Object.defineProperty(instance, 'prisma', {
          configurable: false,
          get() {
            const store = asyncLocalStorage.getStore();
            if (!store) return prevClient;
            return store.txClient;
          },
        });
      });
  }
}
