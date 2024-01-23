import { User } from '@prisma/client';

export class UserDto {
  readonly uuid: string;

  readonly email: string;

  readonly nickname: string;

  static of({ uuid, email, nickname }: User) {
    return { uuid, email, nickname };
  }
}
