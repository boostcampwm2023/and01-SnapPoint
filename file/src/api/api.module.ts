import { Module } from '@nestjs/common';
import { UploadModule } from '@/upload/upload.module';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { FileApiController } from './file-api/file-api.controller';

@Module({
  imports: [
    UploadModule,
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
    ApiModule,
  ],
  controllers: [FileApiController],
  providers: [],
})
export class ApiModule {}
