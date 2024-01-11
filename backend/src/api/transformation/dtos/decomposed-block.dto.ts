export class DecomposedBlockDto {
  readonly uuid: string;

  readonly content: string;

  readonly type: string;

  readonly order: number;

  readonly postUuid: string;

  readonly latitude?: number;

  readonly longitude?: number;
}
