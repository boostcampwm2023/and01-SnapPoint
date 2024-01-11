export class DecomposedFileDto {
  readonly uuid: string;

  readonly source: 'block';

  readonly sourceUuid: string;

  readonly thumbnailUuid?: string;
}
