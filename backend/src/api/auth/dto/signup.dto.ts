import { User } from '@prisma/client';

export class SignUpDto {
  readonly id: number;
  readonly uuid: string;
  readonly email: string;
  readonly nickname: string;
  readonly createdAt: Date;
  readonly modifiedAt: Date;
  readonly isDeleted: boolean;

  static of(user: User): SignUpDto {
    return {
      id: user.id,
      uuid: user.uuid,
      email: user.email,
      nickname: user.nickname,
      createdAt: user.createdAt,
      modifiedAt: user.modifiedAt,
      isDeleted: user.isDeleted,
    };
  }
}
