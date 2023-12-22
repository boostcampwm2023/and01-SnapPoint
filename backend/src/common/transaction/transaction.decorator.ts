import { SetMetadata } from '@nestjs/common';
import { TRANSACTIONAL } from './transaction.key';

export const Transactional = (): MethodDecorator => SetMetadata(TRANSACTIONAL, true);
