export class CreateBlockDto {
  readonly uuid: string;

  readonly content: string;

  readonly type: string;

  readonly latitude?: number;

  readonly longitude?: number;
}
