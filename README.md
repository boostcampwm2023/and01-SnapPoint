# SnapPoint
<img src=https://github.com/boostcampwm2023/and01-SnapPoint/assets/85796984/0ea4de60-ea9d-40e8-89ef-3c305b09665e>

# 프로젝트 소개
### **지도로 확인하는 여행 기록, SnapPoint**
- 내 여행을 게시글 형태로 기록할 수 있어요
- 게시글에 추가한 미디어가 촬영된 위치를 스냅포인트 형태로 저장할 수 있어요
- 미디어들은 경로로 이어져 지도에서 한 눈에 확인할 수 있어요

## 팀원

|K022 안언수|K025 원승빈|K030 이정건|J081 양희범|J150 주재현|
|:-:|:-:|:-:|:-:|:-:|
|<img src="https://github.com/rbybound.png" width=150>|<img src="https://github.com/wsb7788.png" width=150>|<img src="https://github.com/LJG7123.png" width=150>|<img src="https://github.com/takeny1998.png" width=150>|<img src="https://github.com/joojae02.png" width=150>|
|[@rbybound](https://github.com/rbybound)|[@wsb7788](https://github.com/wsb7788)|[@LJG7123](https://github.com/LJG7123)|[@takeny1998](https://github.com/takeny1998)|[@joojae02](https://github.com/joojae02)|

## 주요 기능

|게시글 작성|스냅포인트 클릭|경로 확인|
|:-:|:-:|:-:|
|<img src=https://github.com/boostcampwm2023/and01-SnapPoint/assets/85796984/6d2b4a3e-eec6-4b8e-af82-4ecf702e8db7 width=250>|<img src=https://github.com/boostcampwm2023/and01-SnapPoint/assets/85796984/9ef6f719-b067-4c68-84d5-776fd70cf18c width=250>|<img src=https://github.com/boostcampwm2023/and01-SnapPoint/assets/85796984/22650f61-c716-4173-afc8-68493aa958f8 width=250>|

# 기술적 도전을 확인하려면?
### Android 기술적 도전
- [Room을 사용하여 로컬 데이터베이스에 게시글 저장](https://github.com/boostcampwm2023/and01-SnapPoint/wiki/Room%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%98%EC%97%AC-%EB%A1%9C%EC%BB%AC-%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4%EC%97%90-%EA%B2%8C%EC%8B%9C%EA%B8%80-%EC%A0%80%EC%9E%A5) 
- [경로가 전부 보이도록 Zoom Level 조정](https://github.com/boostcampwm2023/and01-SnapPoint/wiki/%EA%B2%BD%EB%A1%9C%EA%B0%80-%EC%A0%84%EB%B6%80-%EB%B3%B4%EC%9D%B4%EB%8F%84%EB%A1%9D-Zoom-Level-%EC%A1%B0%EC%A0%95)
- [게시글 작성 로직](https://github.com/boostcampwm2023/and01-SnapPoint/wiki/%EA%B2%8C%EC%8B%9C%EA%B8%80-%EC%9E%91%EC%84%B1-%EB%A1%9C%EC%A7%81)
- [이미지 최적화](https://github.com/boostcampwm2023/and01-SnapPoint/wiki/%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%B5%9C%EC%A0%81%ED%99%94)
- [마커 최적화](https://github.com/boostcampwm2023/and01-SnapPoint/wiki/%EB%A7%88%EC%BB%A4-%EC%B5%9C%EC%A0%81%ED%99%94)

### Backend 기술적 도전
- [DDD Layered Architecture](https://github.com/boostcampwm2023/and01-SnapPoint/wiki/DDD-Layered-Architecture)
- [메세지 큐 기반 Adaptive Streaming](https://github.com/boostcampwm2023/and01-SnapPoint/wiki/%EB%A9%94%EC%84%B8%EC%A7%80-%ED%81%90-%EA%B8%B0%EB%B0%98-Adaptive-Streaming)
- [Redis를 이용한 Caching](https://github.com/boostcampwm2023/and01-SnapPoint/wiki/Redis%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-Caching)

## 기술 스택 및 도구
**Android**
|분류|Stack|
|-|-|
|Architecture|MVVM|
|DI|Hilt|
|Network|Retrofit, OkHttp, Kotlinx.Serialization|
|Image|Coil|
|Jetpack|Media3, Room, Navigation, DataStore, Exifinterface|
|Asynchronous|Coroutines, Flow|
|Map|Google Maps|
|CD|GitHub Actions|

**Backend**
|분류|Stack|
|-|-|
|Architecture|DDD Layered Architecture|
|Framework|TypeScript, NestJS|
|ORM|Prisma|
|DB|PostgreSQL, PostGIS, Redis|
|Infra|RabbitMQ, Server, Container Registry, Object Storage|
|Media Processing|ffmpeg, sharp|

## 인프라 구성도
![image](https://github.com/boostcampwm2023/and01-SnapPoint/assets/85796984/700ff9b8-1ef7-4dd0-ab54-394774d18d7c)

# 📚문서📚

- [그라운드 룰](https://www.notion.so/2c85275c12b349f9ac7697372e4ac41b?pvs=21)
- [코딩 컨벤션](https://www.notion.so/8b37933773b64129b06838743837f975?pvs=21)
- [백로그 관리](https://www.notion.so/27e9218de40649edbd567a5558052436?pvs=21)
- [피그마](https://www.notion.so/7b09fc51b75d453db6bcedab1ec55f1a?pvs=21)
- [기술 공유](https://www.notion.so/bb8c7ea8f3a345259b3be0c6b1e92670?pvs=4)
