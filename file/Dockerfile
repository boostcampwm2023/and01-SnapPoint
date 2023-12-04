FROM node:20-alpine as builder

WORKDIR /src

COPY . /src

RUN mkdir -p /var/cache/yarn
RUN yarn config set cache-folder /var/cache/yarn

RUN yarn install --immutable --immutable-cache --check-cache

RUN yarn build

FROM node:20-alpine

WORKDIR /src

COPY --from=builder /var/cache/yarn /var/cache/yarn

RUN yarn config set cache-folder /var/cache/yarn

COPY package.json /src

COPY tsconfig.json /src

COPY yarn.lock /src

RUN yarn install --immutable --immutable-cache && \
  rm -rf /var/cache/yarn

COPY --from=builder /src/dist/ dist

EXPOSE 3001

CMD ["yarn", "start", "prod"]