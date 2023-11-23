import { IsOptional, IsString } from 'class-validator';

export class CreatePostDto {
  @IsString()
  readonly userUuid: string;

  @IsOptional()
  @IsString()
  readonly title: string;
}
