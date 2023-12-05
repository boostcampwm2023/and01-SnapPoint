import { NestFactory } from '@nestjs/core';
import { Transport } from '@nestjs/microservices';
import { AppModule } from './app.module';
import { ConfigService } from '@nestjs/config';

async function bootstrap() {
  const appContext = await NestFactory.createApplicationContext(AppModule);
  const configService = appContext.get(ConfigService);

  const app = await NestFactory.createMicroservice(AppModule, {
    transport: Transport.RMQ,
    options: {
      urls: [configService.getOrThrow<string>('RMQ_HOST')],
      queue: configService.getOrThrow<string>('RMQ_QUEUE'),
      queueOptions: { durable: true },
    },
  });
  await app.listen();
}
bootstrap();
