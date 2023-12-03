import { NestFactory } from '@nestjs/core';
import { Transport } from '@nestjs/microservices';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.createMicroservice(AppModule, {
    transport: Transport.RMQ,
    options: {
      urls: ['amqp://localhost'],
      queue: 'media_processing_queue',
      queueOptions: { durable: true },
    },
  });
  await app.listen();
}
bootstrap();
