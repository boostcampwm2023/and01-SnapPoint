import { applyDecorators, SetMetadata } from '@nestjs/common';
import { TRANSACTIONAL } from './transaction.key';

export const Transactional = (): MethodDecorator => {
  return applyDecorators(SetMetadata(TRANSACTIONAL, true));
};
