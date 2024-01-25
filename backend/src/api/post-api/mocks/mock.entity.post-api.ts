import { Block, Post, User } from '@prisma/client';

export const mockPost = (): Post => ({
  id: 1,
  uuid: 'mock-post-uuid',
  userUuid: 'mock-user-uuid',
  title: 'Test Post',
  summary: null,
  createdAt: new Date('2023-11-23T15:02:10.626Z'),
  modifiedAt: new Date('2023-11-23T15:02:10.626Z'),
  isDeleted: false,
});

export const mockUser = (): User => ({
  id: 1,
  uuid: 'mock-user-uuid',
  email: 'mock@example.com',
  nickname: 'mock-user',
  password: 'mock-hashed-password',
  createdAt: new Date('2023-11-23T15:02:10.626Z'),
  modifiedAt: new Date('2023-11-23T15:02:10.626Z'),
  isDeleted: false,
});

export const mockBlocks = (): Block[] => [
  {
    id: 1,
    uuid: 'mock-block-uuid-1',
    postUuid: 'mock-post-uuid',
    order: 1,
    latitude: null,
    longitude: null,
    type: 'text',
    content: 'this is text block',
    createdAt: new Date('2023-11-23T15:02:10.626Z'),
    modifiedAt: new Date('2023-11-23T15:02:10.626Z'),
    isDeleted: false,
  },
];
