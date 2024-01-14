import { SummarizationService } from '@/api/summarization/summarization.service';
import { Controller } from '@nestjs/common';

import { Ctx, MessagePattern, Payload, RmqContext } from '@nestjs/microservices';
import { SummaryPostDto } from './dtos/summary-post.dto';

@Controller('summary')
export class SummarizationController {
  constructor(private readonly summarizationService: SummarizationService) {}

  @MessagePattern({ cmd: 'summary.post' })
  async summarizePost(@Payload() dto: SummaryPostDto, @Ctx() context: RmqContext) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    this.summarizationService
      .summarizePost(dto)
      .then(() => channel.ack(originalMsg))
      .catch(() => channel.reject(originalMsg));
  }
}
