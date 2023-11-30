import { Controller, Get, Query } from '@nestjs/common';
import { SnapPointService } from './snap-point.service';
import { FindSnapPointQuery } from './dtos/find-snap-point.query.dto';
import { validationPipe } from '@/common/pipes/validation.pipe';
import { TransformationService } from '@/api/transformation/transformation.service';
import { NoAuth } from '@/common/decorator/no-auth.decorator';

@Controller('snap-points')
export class SnapPointController {
  constructor(
    private readonly snapPointService: SnapPointService,
    private readonly transformService: TransformationService,
  ) {}

  @Get('/')
  @NoAuth()
  async findSnapPoint(@Query(validationPipe) query: FindSnapPointQuery) {
    const dto = this.transformService.toFindSnapPointDto(query);
    return this.snapPointService.findSnapPoint(dto);
  }
}
