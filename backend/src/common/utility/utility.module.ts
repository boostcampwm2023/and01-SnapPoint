import { Module } from '@nestjs/common';
import { UtilityService } from './utility.service';

@Module({
  providers: [UtilityService],
  exports: [UtilityService],
})
export class UtilityModule {}
