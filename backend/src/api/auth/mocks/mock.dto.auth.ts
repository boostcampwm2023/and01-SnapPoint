import { SignInDto } from '../dto/sign-in.dto';
import { SignUpDto } from '../dto/sign-up.dto';
import { UserDto } from '../dto/user.dto';

export const mockSignInDto = (): SignInDto => ({
  email: 'test@example.com',
  password: 'password',
});

export const mockSignUpDto = (): SignUpDto => ({
  email: 'test@example.com',
  password: 'password123',
  nickname: 'test-user',
});

export const mockUserDto = (): UserDto => ({
  uuid: 'mock-uuid',
  email: 'test@example.com',
  nickname: 'test-user',
});
