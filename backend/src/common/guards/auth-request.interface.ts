import { Request } from 'express';
import { UserPayload } from './user-payload.interface';

export interface AuthRequest extends Request {
  user: UserPayload;
}
