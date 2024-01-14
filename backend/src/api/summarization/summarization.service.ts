import { PostService } from './../../domain/post/post.service';
import { HttpService } from '@nestjs/axios';
import { HttpException, Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { firstValueFrom, map } from 'rxjs';
import { SummaryPostDto } from './dtos/summary-post.dto';

@Injectable()
export class SummarizationService {
  private readonly summaryApiUrl = 'https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize';
  private readonly maxContentLength = 1900;
  private readonly maxSentenceLength = 200;
  private readonly minWordCount = 5;
  private readonly summaryClientId = this.configService.getOrThrow('NCP_AI_CLIENT_ID');
  private readonly summaryClientSecret = this.configService.getOrThrow('NCP_AI_CLIENT_SECRET');

  constructor(
    private httpService: HttpService,
    private configService: ConfigService,
    private postService: PostService,
  ) {}

  async summarizePost({ post, blocks }: SummaryPostDto) {
    const { uuid, title } = post;
    const content = this.prepareContent(blocks);

    // Summary AI의 동작 조건에 맞지 않는 경우, 첫 번째 텍스트 블록 내용을 저장한다.
    if (!this.isContentValid(content)) {
      return this.postService.updatePost({
        where: { uuid },
        data: { summary: this.getFirstTextBlockContent(blocks) },
      });
    }

    const summary$ = this.summarizeContent(title, content);

    // 요약이 되지 않을 경우 (NULL) 첫 번째 텍스트 블록 내용을 저장한다.
    if (summary$ === null) {
      return this.postService.updatePost({
        where: { uuid },
        data: { summary: this.getFirstTextBlockContent(blocks) },
      });
    }

    const { summary } = await firstValueFrom(summary$);
    return this.postService.updatePost({
      where: { uuid },
      data: { summary },
    });
  }

  private summarizeContent(title: string, content: string) {
    const data = {
      document: { title, content },
      option: { language: 'ko', model: 'general', tone: 3, summaryCount: 1 },
    };

    const config = {
      headers: {
        'X-NCP-APIGW-API-KEY-ID': this.summaryClientId,
        'X-NCP-APIGW-API-KEY': this.summaryClientSecret,
      },
    };

    try {
      const summary$ = this.httpService
        .post<{ summary: string }>(this.summaryApiUrl, data, config)
        .pipe(map((res) => res.data));

      return summary$;
    } catch (error) {
      // API 연결 오류일 경우 NULL을 반환한다.
      if (error instanceof HttpException) return null;
      throw error;
    }
  }

  private prepareContent(blocks: { content: string; type: string }[]): string {
    return blocks
      .map((block) => block.content)
      .join('\n')
      .substring(0, this.maxContentLength);
  }

  private isContentValid(content: string): boolean {
    const sentences = content.split(/[.?!]\s/).filter((sentence) => sentence.trim() !== '');
    return sentences.every(this.isValidSentence) && sentences.length > 0;
  }

  private isValidSentence = (sentence: string): boolean => {
    return sentence.length <= this.maxSentenceLength && this.hasEnoughWords(sentence);
  };

  private hasEnoughWords(sentence: string): boolean {
    return sentence.trim().split(/\s+/).length >= this.minWordCount;
  }

  private getFirstTextBlockContent(blocks: { content: string; type: string }[]): string {
    const textBlock = blocks.find((block) => block.type === 'text');
    return textBlock ? textBlock.content : blocks[0].content;
  }
}
