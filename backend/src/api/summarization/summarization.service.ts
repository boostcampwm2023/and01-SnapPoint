import { HttpService } from '@nestjs/axios';
import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class SummarizationService {
  private readonly summaryApiUrl = 'https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize';
  private readonly maxContentLength = 1900;
  private readonly maxSentenceLength = 200;
  private readonly minWordCount = 5;

  constructor(
    private httpService: HttpService,
    private configService: ConfigService,
  ) {}

  async summarizeContent(title: string, content: string): Promise<string | null> {
    try {
      const response = await this.httpService
        .post<{ summary: string }>(
          this.summaryApiUrl,
          {
            document: { title, content },
            option: { language: 'ko', model: 'general', tone: 3, summaryCount: 1 },
          },
          {
            headers: this.getApiHeaders(),
          },
        )
        .toPromise();

      return response!.data.summary;
    } catch (error) {
      Logger.error(error.response?.data);
      return null;
    }
  }

  async summarizePost(title: string, blocks: { content: string; type: string }[]): Promise<string> {
    const content = this.prepareContent(blocks);

    const summary: string | null = this.isContentValid(content) ? await this.summarizeContent(title, content) : null;
    return summary ? summary : this.getFirstTextBlockContent(blocks);
  }

  private prepareContent(blocks: { content: string; type: string }[]): string {
    return blocks
      .map((block) => block.content)
      .join('. ')
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

  private getApiHeaders() {
    return {
      'X-NCP-APIGW-API-KEY-ID': this.configService.getOrThrow('NCP_AI_CLIENT_ID'),
      'X-NCP-APIGW-API-KEY': this.configService.getOrThrow('NCP_AI_CLIENT_SECRET'),
    };
  }
}
