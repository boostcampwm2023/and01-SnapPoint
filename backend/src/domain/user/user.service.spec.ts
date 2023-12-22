import { Test, TestingModule } from '@nestjs/testing';
import { UserService } from './user.service';
import { User } from '@prisma/client';
import { DeepMockProxy } from 'jest-mock-extended';
import * as bcrypt from 'bcrypt';
import { MockPrismaProvider } from '@/common/mocks/mock.prisma';

describe('UserService', () => {
  let userService: UserService;
  let prisma: DeepMockProxy<MockPrismaProvider>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [UserService],
    }).compile();

    userService = module.get<UserService>(UserService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('유저 생성', () => {
    const createUserDto = {
      email: 'test@example.com',
      password: 'password123',
      nickname: 'testuser',
    };
    const hashedPassword = 'hashedPassword';
    const userMock: User = {
      id: 1,
      email: createUserDto.email,
      password: hashedPassword,
      nickname: createUserDto.nickname,
      uuid: 'testUuid',
      createdAt: new Date(),
      modifiedAt: new Date(),
      isDeleted: false,
    };

    it('정상 입력인 경우 새로운 유저를 생성한다.', async () => {
      jest.spyOn(bcrypt, 'hash').mockImplementation(() => Promise.resolve(hashedPassword));

      prisma.get().user.findUnique.mockResolvedValueOnce(null);
      prisma.get().user.create.mockResolvedValueOnce(userMock);

      const result = await userService.create(createUserDto);

      expect(result).toEqual(userMock);
    });
  });
});
