import { Injectable } from '@nestjs/common';
import { FindSnapPointQuery } from '../snap-point/dtos/find-snap-point.query.dto';

@Injectable()
export class TransformationService {
  private parseLatLon(latLon: string) {
    const [lat, lon] = latLon.split(',').map((s) => parseFloat(s.trim()));
    return { latitude: lat, longitude: lon };
  }

  toFindSnapPointDto(query: FindSnapPointQuery) {
    const { from, to } = query;

    const { latitude: fromLat, longitude: fromLon } = this.parseLatLon(from);
    const { latitude: toLat, longitude: toLon } = this.parseLatLon(to);

    return {
      latitudeMin: Math.min(fromLat, toLat),
      latitudeMax: Math.max(fromLat, toLat),
      longitudeMin: Math.min(fromLon, toLon),
      longitudeMax: Math.max(fromLon, toLon),
    };
  }
}
