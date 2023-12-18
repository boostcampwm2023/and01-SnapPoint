import { Repository } from '../interfaces/repository.interface';
import { Injectable, OnModuleInit } from '@nestjs/common';
import { DiscoveryService, MetadataScanner, Reflector } from '@nestjs/core';
import { PrismaService } from '../prisma/prisma.service';
import { AsyncLocalStorage } from 'async_hooks';
import { TRANSACTIONAL_KEY } from './transacion.key';
import { Prisma } from '@prisma/client';

@Injectable()
export class TransactionManager implements OnModuleInit {
  private readonly asyncLocalStorage = new AsyncLocalStorage<{ txClient: Prisma.TransactionClient }>();

  constructor(
    private readonly discoverService: DiscoveryService,
    private readonly metadataScanner: MetadataScanner,
    private readonly reflector: Reflector,
    private readonly prisma: PrismaService,
  ) {}

  /**
   * Initializes the transaction manager, wrapping methods and repositories with transactional logic.
   */
  onModuleInit() {
    this.wrapDecorators();
    this.wrapRepository();
  }

  /**
   * Retrieves static instances from the discovery service.
   * @returns Array of static instances.
   */
  private getStaticInstances(): any[] {
    return this.discoverService
      .getProviders()
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

    return async function (...args: any[]) {
      // Wrapping the original method with transaction logic.
      return await prisma.$transaction(async (txClient) =>
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
        if (this.reflector.get(TRANSACTIONAL_KEY, method)) {
          instance[name] = this.wrapMethods(method, instance);
        }
      });
    });
  }

  /**
   * Wraps repositories to use the transactional client from AsyncLocalStorage.
   */
  wrapRepository() {
    const { asyncLocalStorage } = this;
    const instances = this.getStaticInstances();

    instances
      .filter((instance) => instance instanceof Repository)
      .forEach((instance) => {
        const prevClient = instance.prisma;

        // Defining a getter to provide the transactional client.
        Object.defineProperty(instance, 'prisma', {
          configurable: false,
          get() {
            const store = asyncLocalStorage.getStore();
            return store ? store.txClient : prevClient;
          },
        });
      });
  }
}
