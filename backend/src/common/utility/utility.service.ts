import { Injectable } from '@nestjs/common';

@Injectable()
export class UtilityService {
  constructor() {}

  /**
   * 두 지점의 거리를 계산하고, KM 단위로 반환한다.
   * @param latMin 위도의 최솟값
   * @param lonMin 경도의 최솟값
   * @param latMax 위도의 최댓값
   * @param lonMax 경도의 최댓값
   * @returns 두 지점의 거리 (KM 단위)
   */
  calDistance(latMin: number, lonMin: number, latMax: number, lonMax: number) {
    const R = 6371000; // 지구 반지름 (미터 단위)
    const rad = Math.PI / 180; // 도를 라디안으로 변환

    const x = (lonMax * rad - lonMin * rad) * Math.cos((latMin * rad + latMax * rad) / 2);
    const y = latMax * rad - latMin * rad;

    return (Math.sqrt(x * x + y * y) * R) / 1000;
  }

  toMapFromArray<K, V>(items: V[], extractKeyFn: (item: V) => K): Map<K, V[]> {
    const map = new Map<K, V[]>();
    items.forEach((item) => {
      const key = extractKeyFn(item);
      const collection = map.get(key) || [];
      collection.push(item);
      map.set(key, collection);
    });
    return map;
  }

  toUniqueMapFromArray<K, V>(items: V[], extractKeyFn: (item: V) => K): Map<K, V> {
    const map = new Map<K, V>();
    items.forEach((item) => {
      const key = extractKeyFn(item);
      map.set(key, item);
    });
    return map;
  }

  toTransMapFromArray<T, K, V>(items: T[], extractKeyFn: (item: T) => K, transformFn: (item: T) => V): Map<K, V[]> {
    const map = new Map<K, V[]>();
    items.forEach((item) => {
      const key = extractKeyFn(item);
      const collection = map.get(key) || [];
      collection.push(transformFn(item));
      map.set(key, collection);
    });
    return map;
  }
}
