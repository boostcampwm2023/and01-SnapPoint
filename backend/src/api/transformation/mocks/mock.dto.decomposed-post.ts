import { plainToInstance } from 'class-transformer';
import { DecomposedPostDataDto } from '../dtos/decomposed-post-data.dto';
import { DecomposedBlockDto } from '../dtos/decomposed-block.dto';
import { DecomposedPostDto } from '../dtos/decomposed-post.dto';
import { DecomposedFileDto } from '../dtos/decomposed-file.dto';

export const mockDecomposedImagePostDto = () =>
  plainToInstance(DecomposedPostDataDto, {
    post: plainToInstance(DecomposedPostDto, {
      uuid: 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e',
      title: '2박 3일 동해 여행 관람기',
    }),
    blocks: plainToInstance(DecomposedBlockDto, [
      {
        uuid: '773b3c42-7422-49f3-babc-9309d1bc3fc2',
        postUuid: 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e',
        content:
          '설악산, 동해 바다, 온천 등 천혜의 관광 요소를 갖춘 속초는 여행 마니아의 많은 사랑을 받는 곳이에요. 여름이면 푸른 동해, 가을이면 단풍으로 물든 설악산, 겨울이면 설악의 설경과 뜨거운 온천욕이 유혹하는 곳이죠. 설악산 등산 후 온천을 하고 동해 바다를 바라보며 회 한 접시 먹을 수 있는 호사를 누릴 수 있는 매력적인 여행지 속초의 보물들을 소개해요.',
        type: 'text',
        latitude: undefined,
        longitude: undefined,
        order: 0,
      },
      {
        uuid: '3313ce67-7c6e-492c-abee-e27320da0eba',
        postUuid: 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e',
        content:
          '속초시 설악동에 위치한 고찰로 신라의 승려 자장이 창건한 절이에요. 외설악 쪽에서 설악산을 오르게 되면 그 출발점이 되는 곳이에요. 한국전쟁 당시 인근 건봉사가 소실된 후 영동지방을 대표하는 사찰의 위상을 갖게 되었죠. 창건 당시 주조한 1400년 된 범종과 극락보전, 보제루, 향성사지 3층 석탑 등의 문화재가 있어요. 넓은 경내는 천천히 산책하며 돌아보기 좋아요.',
        type: 'media',
        latitude: 60.1414,
        longitude: 120.3538,
        order: 1,
      },
      {
        uuid: 'fc2b6adb-2c28-44af-89f9-4b56313e1a4d',
        postUuid: 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e',
        content:
          '설악산의 비선대에서 대청봉으로 오르는 8km 구간에 펼쳐진 계곡이에요. 천불동이라는 이름은 계곡 양쪽의 기암절벽이 천 개의 불상이 늘어서 있는 듯한 형상이라는 데서 유래했어요. 설악산 대표 탐방코스로 특히 가을에는 단풍을 즐기러 온 여행객으로 붐비죠. 설악동 소공원에서 시작되는 탐방코스 중간에는 와선대, 비선대, 귀면암, 오련폭포, 양폭 등 빼어난 경관지가 이어져요. 양폭 대피소까지 소요 시간은 왕복 약 6~7시간이에요.',
        type: 'text',
        latitude: undefined,
        longitude: undefined,
        order: 2,
      },
      {
        uuid: '21846560-dc13-4cdd-ab1a-0127b4dd4f65',
        postUuid: 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e',
        content:
          '설악산 토왕골에 있는 16m 높이의 아름다운 폭포예요. 폭포 속에 사는 용에게 처녀를 바쳐 하늘로 올려보내 심한 가뭄을 면했다는 전설이 있어요. 비룡폭포까지 오르는 길은 비교적 쉬운 탐방코스로 설악동 소공원에서 시작하여 육담폭포를 거쳐 오르는데 중간에 많은 계곡과 작은 폭포를 만날 수 있어요. 비룡폭포에서 좀 더 오르면 멀리서 토왕성폭포의 장관을 볼 수 있는 토왕성폭포 전망대가 있어요. 비룡폭포까지는 왕복 약 3시간 소요돼요.',
        type: 'text',
        latitude: undefined,
        longitude: undefined,
        order: 3,
      },
    ]),
    files: plainToInstance(DecomposedFileDto, [
      {
        uuid: '89eff098-2c4d-48e7-925c-4ab16b3f5865',
        source: 'block',
        sourceUuid: '3313ce67-7c6e-492c-abee-e27320da0eba',
      },
    ]),
  });

export const mockDecomposedVideoPostDto = () =>
  plainToInstance(DecomposedPostDataDto, {
    post: {
      uuid: 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e',
      title: '2박 3일 동해 여행 관람기',
    },
    blocks: plainToInstance(DecomposedBlockDto, [
      {
        uuid: '773b3c42-7422-49f3-babc-9309d1bc3fc2',
        postUuid: 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e',
        content:
          '설악산, 동해 바다, 온천 등 천혜의 관광 요소를 갖춘 속초는 여행 마니아의 많은 사랑을 받는 곳이에요. 여름이면 푸른 동해, 가을이면 단풍으로 물든 설악산, 겨울이면 설악의 설경과 뜨거운 온천욕이 유혹하는 곳이죠. 설악산 등산 후 온천을 하고 동해 바다를 바라보며 회 한 접시 먹을 수 있는 호사를 누릴 수 있는 매력적인 여행지 속초의 보물들을 소개해요.',
        type: 'text',
        latitude: undefined,
        longitude: undefined,
        order: 0,
      },
      {
        uuid: '3313ce67-7c6e-492c-abee-e27320da0eba',
        postUuid: 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e',
        content:
          '속초시 설악동에 위치한 고찰로 신라의 승려 자장이 창건한 절이에요. 외설악 쪽에서 설악산을 오르게 되면 그 출발점이 되는 곳이에요. 한국전쟁 당시 인근 건봉사가 소실된 후 영동지방을 대표하는 사찰의 위상을 갖게 되었죠. 창건 당시 주조한 1400년 된 범종과 극락보전, 보제루, 향성사지 3층 석탑 등의 문화재가 있어요. 넓은 경내는 천천히 산책하며 돌아보기 좋아요.',
        type: 'media',
        latitude: 60.1414,
        longitude: 120.3538,
        order: 1,
      },
      {
        uuid: 'fc2b6adb-2c28-44af-89f9-4b56313e1a4d',
        postUuid: 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e',
        content:
          '설악산의 비선대에서 대청봉으로 오르는 8km 구간에 펼쳐진 계곡이에요. 천불동이라는 이름은 계곡 양쪽의 기암절벽이 천 개의 불상이 늘어서 있는 듯한 형상이라는 데서 유래했어요. 설악산 대표 탐방코스로 특히 가을에는 단풍을 즐기러 온 여행객으로 붐비죠. 설악동 소공원에서 시작되는 탐방코스 중간에는 와선대, 비선대, 귀면암, 오련폭포, 양폭 등 빼어난 경관지가 이어져요. 양폭 대피소까지 소요 시간은 왕복 약 6~7시간이에요.',
        type: 'text',
        latitude: undefined,
        longitude: undefined,
        order: 2,
      },
      {
        uuid: '21846560-dc13-4cdd-ab1a-0127b4dd4f65',
        postUuid: 'e9aa6e8f-40ea-44d1-9667-71ac67c9346e',
        content:
          '설악산 토왕골에 있는 16m 높이의 아름다운 폭포예요. 폭포 속에 사는 용에게 처녀를 바쳐 하늘로 올려보내 심한 가뭄을 면했다는 전설이 있어요. 비룡폭포까지 오르는 길은 비교적 쉬운 탐방코스로 설악동 소공원에서 시작하여 육담폭포를 거쳐 오르는데 중간에 많은 계곡과 작은 폭포를 만날 수 있어요. 비룡폭포에서 좀 더 오르면 멀리서 토왕성폭포의 장관을 볼 수 있는 토왕성폭포 전망대가 있어요. 비룡폭포까지는 왕복 약 3시간 소요돼요.',
        type: 'text',
        latitude: undefined,
        longitude: undefined,
        order: 3,
      },
    ]),
    files: plainToInstance(DecomposedFileDto, [
      {
        uuid: '33405008-9fdf-4852-957e-ab0aecfcdd98',
        source: 'block',
        sourceUuid: '3313ce67-7c6e-492c-abee-e27320da0eba',
        thumbnailUuid: '120bb594-dc87-4b9e-aed5-e1f291d566e2',
      },
      {
        uuid: '120bb594-dc87-4b9e-aed5-e1f291d566e2',
        source: 'block',
        sourceUuid: '3313ce67-7c6e-492c-abee-e27320da0eba',
      },
    ]),
  });
