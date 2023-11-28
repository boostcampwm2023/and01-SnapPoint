import { Injectable } from '@nestjs/common';
import { Post, Prisma } from '@prisma/client';
import { PrismaProvider } from '@/common/prisma/prisma.provider';

@Injectable()
export class PostService {
  constructor(private prisma: PrismaProvider) {}

  async createPost(data: Prisma.PostCreateInput) {
    return this.prisma.get().post.create({ data });
  }

  async findPost(postWhereUniqueInput: Prisma.PostWhereUniqueInput): Promise<Post | null> {
    return this.prisma.get().post.findUnique({
      where: postWhereUniqueInput,
    });
  }

  async findPosts(params: {
    skip?: number;
    take?: number;
    cursor?: Prisma.PostWhereUniqueInput;
    where?: Prisma.PostWhereInput;
    orderBy?: Prisma.PostOrderByWithRelationInput;
  }): Promise<Post[]> {
    const { skip, take, cursor, where, orderBy } = params;
    return this.prisma.get().post.findMany({
      skip,
      take,
      cursor,
      where,
      orderBy,
    });
  }

  async updatePost(params: { where: Prisma.PostWhereUniqueInput; data: Prisma.PostUpdateInput }) {
    const { data, where } = params;
    return this.prisma.get().post.update({
      data,
      where,
    });
  }

  async deletePost(where: Prisma.PostWhereUniqueInput): Promise<Post> {
    return this.prisma.get().post.update({
      data: { isDeleted: true },
      where,
    });
  }
}
