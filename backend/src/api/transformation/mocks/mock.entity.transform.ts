import { UserPayload } from '@/common/guards/user-payload.interface';
import { Post, Block, File } from '@prisma/client';

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

export const mockUserPayload = (): UserPayload => ({
  uuid: 'mock-user-uuid',
  email: 'mock@example.com',
  nickname: 'mock-user',
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
  {
    id: 2,
    uuid: 'mock-block-uuid-2',
    postUuid: 'mock-post-uuid',
    order: 2,
    latitude: 36.125,
    longitude: 127.538,
    type: 'media',
    content: 'this is media block',
    createdAt: new Date('2023-11-23T15:02:10.626Z'),
    modifiedAt: new Date('2023-11-23T15:02:10.626Z'),
    isDeleted: false,
  },
];

export const mockFiles = (): File[] => [
  {
    id: 1,
    uuid: 'mock-block-uuid-1',
    userUuid: 'mock-user-uuid',
    source: 'block',
    sourceUuid: 'mock-block-uuid-2',
    url: 'mock-url',
    mimeType: 'image/webp',
    isProcessed: false,
    thumbnailUuid: null,
    createdAt: new Date('2023-11-23T15:02:10.626Z'),
    isDeleted: false,
  },
];
