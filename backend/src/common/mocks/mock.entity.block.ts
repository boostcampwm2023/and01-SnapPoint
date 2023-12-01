import { Block } from '@/domain/block/entites/block.entity';

export const mockBlockEntities: Block[] = [
  {
    id: 1,
    uuid: 'mock-block-uuid-1',
    postUuid: 'mock-post-uuid',
    content: 'text-block',
    order: 0,
    createdAt: new Date('2023-11-23T15:02:10.626Z'),
    modifiedAt: new Date('2023-11-23T15:02:10.626Z'),
    type: 'text',
    isDeleted: false,
  },
  {
    id: 2,
    uuid: 'mock-block-uuid-2',
    postUuid: 'mock-post-uuid',
    content: 'media-block',
    order: 1,
    createdAt: new Date('2023-11-23T15:02:10.626Z'),
    modifiedAt: new Date('2023-11-23T15:02:10.626Z'),
    type: 'media',
    isDeleted: false,
    latitude: 8.1414,
    longitude: -74.3538,
  },
];
