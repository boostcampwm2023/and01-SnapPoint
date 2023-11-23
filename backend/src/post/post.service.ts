import { ConflictException, Injectable } from '@nestjs/common';
import { Post, Prisma } from '@prisma/client';
import { PrismaProvider } from '@/prisma/prisma.provider';
import { CreatePostDto } from './dtos/create-post.dto';

@Injectable()
export class PostService {
  constructor(private prisma: PrismaProvider) {}

  async create(createPostDto: CreatePostDto): Promise<Post> {
    return this.prisma.get().post.create({
      data: createPostDto,
    });
  }

  async findOne(uuid: string): Promise<Post> {
    const post = await this.prisma.get().post.findUnique({ where: { uuid } });

    return post;
  }

  async findMany(where?: Prisma.PostWhereInput): Promise<Post[] | null> {
    return this.prisma.get().post.findMany({
      where,
    });
  }

  async update(uuid: string, updatePostDto: Prisma.PostUpdateInput) {
    const post = await this.findOne(uuid);

    return this.prisma.get().post.update({
      data: updatePostDto,
      where: { uuid: post.uuid },
    });
  }

  async publish(uuid: string) {
    const post = await this.findOne(uuid);

    if (post.isPublished) {
      throw new ConflictException(`Post with UUID: ${post.uuid} is already published.`);
    }

    return this.prisma.get().post.update({ data: { isPublished: true }, where: { uuid: post.uuid } });
  }
}
