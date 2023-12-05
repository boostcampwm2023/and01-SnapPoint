FROM node:20-alpine

RUN mkdir -p /src/app
WORKDIR /src/app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

CMD ["node", "dist/main.js"]