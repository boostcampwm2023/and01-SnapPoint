import { Injectable } from '@nestjs/common';
import { FindNearbyPostQuery } from '../post-api/dtos/find-nearby-post.query.dto';

@Injectable()
export class TransformationService {
  private parseLatLon(latLon: string) {
    const [lat, lon] = latLon.split(',').map((s) => parseFloat(s.trim()));
    return { latitude: lat, longitude: lon };
  }

  toNearbyPostDtoFromQuery(query: FindNearbyPostQuery) {
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

  toMapFromArray<T, K, V>(items: T[], keyExtractor: (item: T) => K, valueTransformer: (item: T) => V): Map<K, V[]> {
    const map = new Map<K, V[]>();
    items.forEach((item) => {
      const key = keyExtractor(item);
      const collection = map.get(key) || [];
      collection.push(valueTransformer(item));
      map.set(key, collection);
    });
    return map;
  }
}
