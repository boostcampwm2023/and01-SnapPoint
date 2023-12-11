import { Global, Module } from '@nestjs/common';
import { ImageModule } from './image/image.module';
import { ConfigModule } from '@nestjs/config';
import { StorageModule } from './storage/storage.module';
import { VideoModule } from './video/video.module';

@Global()
@Module({
  imports: [
    ImageModule,
    StorageModule,
    ConfigModule.forRoot({
      cache: true,
      isGlobal: true,
    }),
    VideoModule,
  ],
})
export class AppModule {}
