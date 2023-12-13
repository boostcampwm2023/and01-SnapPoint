export class Block {
  id: number;
  uuid: string;
  postUuid: string;
  type: string;
  order: number;
  content: string;
  latitude?: number;
  longitude?: number;
  createdAt: Date;
  modifiedAt: Date;
  isDeleted: boolean;
}
