import { Post } from '@prisma/client';

export const mockPostEntity: Post = {
  id: 1,
  uuid: 'mock-post-uuid',
  userUuid: 'mock-user-uuid',
  title: 'Test Post',
  createdAt: new Date('2023-11-23T15:02:10.626Z'),
  modifiedAt: new Date('2023-11-23T15:02:10.626Z'),
  isDeleted: false,
  isPublished: false,
};
