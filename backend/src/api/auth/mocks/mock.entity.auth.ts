import { User } from '@prisma/client';

export const mockUser = (): User => ({
  id: 1,
  email: 'test@example.com',
  uuid: 'mock-uuid',
  password: 'hashed-password',
  nickname: 'test-user',
  createdAt: new Date(),
  modifiedAt: new Date(),
  isDeleted: false,
});

export const mockAccessToken = () => 'mock-access-token';
export const mockRefreshToken = () => 'mock-refresh-token';
