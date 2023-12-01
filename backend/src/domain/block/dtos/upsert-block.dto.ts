export class UpsertBlockDto {
  readonly uuid: string;

  readonly content: string;

  readonly type: string;

  readonly order: number;

  readonly latitude?: number;

  readonly longitude?: number;

  readonly isDeleted: boolean;
}
