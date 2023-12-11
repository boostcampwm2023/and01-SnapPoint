import { File } from '@prisma/client';

export const mockFileEntities: File[] = [
  {
    id: 1,
    uuid: 'mock-file-uuid-1',
    userUuid: 'mock-user-uuid',
    mimeType: 'image/jpeg',
    url: 'https://mock.storage.com/mock-file-uuid-1',
    createdAt: new Date('2023-11-23T15:02:10.626Z'),
    isDeleted: false,
    isProcessed: false,
    source: null,
    sourceUuid: null,
    thumbnailUuid: null,
  },
  {
    id: 2,
    uuid: 'mock-file-uuid-2',
    userUuid: 'mock-user-uuid',
    mimeType: 'image/jpeg',
    url: 'https://mock.storage.com/mock-file-uuid-2',
    createdAt: new Date('2023-11-23T15:02:10.626Z'),
    isDeleted: false,
    isProcessed: false,
    source: null,
    sourceUuid: null,
    thumbnailUuid: null,
  },
];
