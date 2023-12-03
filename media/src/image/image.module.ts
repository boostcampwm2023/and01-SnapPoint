import { Module } from '@nestjs/common';
import { ImageController } from './image.controller';
import { ImageService } from './image.service';
import { StorageModule } from '@/storage/storage.module';
import { ClientsModule, Transport } from '@nestjs/microservices';

@Module({
  imports: [
    StorageModule,
    ClientsModule.register([
      {
        name: 'MAIN_SERVICE',
        transport: Transport.RMQ,
        options: {
          urls: ['amqp://localhost'],
          queue: 'file_processing_queue',
          queueOptions: {
            durable: true,
          },
        },
      },
    ]),
  ],
  controllers: [ImageController],
  providers: [ImageService],
})
export class ImageModule {}
