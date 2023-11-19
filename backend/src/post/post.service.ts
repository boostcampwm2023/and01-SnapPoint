import { Injectable } from '@nestjs/common';
import { Post, Prisma } from '@prisma/client';
import { PrismaProvider } from '@/prisma.service';
import { CreatePostDto } from './dtos/create-post.dto';

@Injectable()
export class PostService {
  constructor(private prisma: PrismaProvider) {}

  async create(createPostDto: CreatePostDto): Promise<Post> {
    const { title } = createPostDto;
    const userUuid = 'test';

    const createdPost = this.prisma.post.create({
      data: {
        title,
        userUuid,
      },
    });

    return createdPost;
  }

  async post(where: Prisma.PostWhereUniqueInput): Promise<Post | null> {
    return this.prisma.post.findUnique({
      where,
    });
  }

  async posts(where?: Prisma.PostWhereInput): Promise<Post[] | null> {
    return this.prisma.post.findMany({
      where,
    });
  }
}
