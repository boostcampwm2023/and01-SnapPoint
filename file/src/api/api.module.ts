import { Module } from '@nestjs/common';
import { UploadModule } from '@/upload/upload.module';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { FileApiController } from './file-api/file-api.controller';
import { ConfigModule, ConfigService } from '@nestjs/config';

@Module({
  imports: [
    UploadModule,
    ClientsModule.registerAsync([
      {
        name: 'DATA_SERVICE',
        imports: [ConfigModule],
        useFactory: async (configService: ConfigService) => ({
          transport: Transport.RMQ,
          options: {
            urls: [configService.getOrThrow<string>('RMQ_HOST')],
            queue: configService.getOrThrow<string>('RMQ_DATA_QUEUE'),
            queueOptions: {
              durable: true,
            },
          },
        }),
        inject: [ConfigService],
      },
    ]),
  ],
  controllers: [FileApiController],
  providers: [],
})
export class ApiModule {}
