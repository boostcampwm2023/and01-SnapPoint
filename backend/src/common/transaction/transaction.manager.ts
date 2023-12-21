import { Inject, Injectable, OnModuleInit } from '@nestjs/common';
import { DiscoveryService, MetadataScanner, Reflector } from '@nestjs/core';
import { TRANSACTIONAL } from './transaction.key';
import { TransactionStorage } from './transaction.storage';
import { PrismaClient } from '@prisma/client';

@Injectable()
export class TransactionManager implements OnModuleInit {
  private readonly prisma = new PrismaClient();

  constructor(
    @Inject('TRANSACTION_STORAGE') private readonly asyncLocalStorage: TransactionStorage,
    private readonly discoverService: DiscoveryService,
    private readonly metadataScanner: MetadataScanner,
    private readonly reflector: Reflector,
  ) {}

  /**
   * Initializes the transaction manager, wrapping methods and repositories with transactional logic.
   */
  onModuleInit() {
    this.wrapDecorators();
  }

  /**
   * Retrieves static instances from the discovery service.
   * @returns Array of static instances.
   */
  private getStaticInstances(): any[] {
    return this.discoverService
      .getProviders()
      .filter((wrapper) => wrapper.isDependencyTreeStatic())
      .filter((wrapper) => wrapper.instance)
      .map((wrapper) => wrapper.instance);
  }

  /**
   * Wraps a method with transactional logic.
   * @param method The original method to wrap.
   * @param instance The instance of the class containing the method.
   * @returns The wrapped method.
   */
  private wrapMethods(method: any, instance: any) {
    const { prisma, asyncLocalStorage } = this;

    // Wrapping the original method with transaction logic.
    return async function (...args: any[]) {
      const store = asyncLocalStorage.getStore();

      if (store) {
        return method.apply(instance, args);
      }

      return prisma.$transaction(async (txClient) =>
        asyncLocalStorage.run({ txClient }, () => method.apply(instance, args)),
      );
    };
  }

  /**
   * Wraps methods annotated with @Transactional.
   */
  wrapDecorators() {
    const instances = this.getStaticInstances();

    instances.forEach((instance) => {
      const prototype = Object.getPrototypeOf(instance);
      const methodNames = this.metadataScanner.getAllMethodNames(prototype);

      methodNames.forEach((name) => {
        const method = instance[name];
        // Wrap only if the method is annotated with @Transactional.
        if (this.reflector.get(TRANSACTIONAL, method)) {
          instance[name] = this.wrapMethods(method, instance);
        }
      });
    });
  }
}
