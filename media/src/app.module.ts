import { Global, Module } from '@nestjs/common';
import { ImageModule } from './image/image.module';
import { ConfigModule } from '@nestjs/config';
import { StorageModule } from './storage/storage.module';
@Global()
@Module({
  imports: [
    ImageModule,
    StorageModule,
    ConfigModule.forRoot({
      cache: true,
      isGlobal: true,
    }),
  ],
})
export class AppModule {}
