export class SummaryPostDto {
  post: { uuid: string; title: string };

  blocks: { content: string; type: string }[];
}
