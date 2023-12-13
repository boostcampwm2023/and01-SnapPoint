# 베이스 이미지
FROM node:20-alpine

# 작업 디렉토리 설정
WORKDIR /src/app

# package.json과 package-lock.json 복사
COPY package*.json ./

# npm 패키지 설치
RUN npm install

# ffmpeg 설치
RUN apk update && apk add ffmpeg && rm -rf /var/cache/apk/*

# 나머지 애플리케이션 파일 복사
COPY . .

# 빌드 실행
RUN npm run build

# 컨테이너 실행 시 실행할 명령
CMD ["node", "dist/main.js"]
