import { applyDecorators, SetMetadata } from '@nestjs/common';
import { TRANSACTIONAL_KEY } from './transacion.key';

export const Transactional = (): MethodDecorator => {
  return applyDecorators(SetMetadata(TRANSACTIONAL_KEY, true));
};
