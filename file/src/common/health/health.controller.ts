import { Controller, Get } from '@nestjs/common';
import {
  HealthCheckService,
  HttpHealthIndicator,
  HealthCheck,
  MicroserviceHealthIndicator,
} from '@nestjs/terminus';
import { Transport } from '@nestjs/microservices';
import { ConfigService } from '@nestjs/config';

@Controller('health')
export class HealthController {
  constructor(
    private health: HealthCheckService,
    private http: HttpHealthIndicator,
    private microService: MicroserviceHealthIndicator,
    private configService: ConfigService,
  ) {}

  @Get()
  @HealthCheck()
  check() {
    return this.health.check([
      () => this.http.pingCheck('HTTP', 'https://docs.nestjs.com'),
      () =>
        this.microService.pingCheck('DATA_MICROSERVICE', {
          transport: Transport.RMQ,
          options: {
            urls: [this.configService.getOrThrow<string>('RMQ_HOST')],
            queue: this.configService.getOrThrow<string>('RMQ_DATA_QUEUE'),
            queueOptions: { durable: true },
          },
        }),
    ]);
  }
}
