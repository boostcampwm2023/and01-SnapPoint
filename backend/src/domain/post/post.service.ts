import { Inject, Injectable } from '@nestjs/common';
import { Post, Prisma } from '@prisma/client';
import { CreatePostDto } from './dtos/create-post.dto';
import { PRISMA_SERVICE, PrismaService } from '@/common/databases/prisma.service';

@Injectable()
export class PostService {
  constructor(@Inject(PRISMA_SERVICE) private readonly prisma: PrismaService) {}

  async createPost(userUuid: string, dto: CreatePostDto) {
    return this.prisma.post.create({ data: { ...dto, userUuid } });
  }

  async findPost(postWhereUniqueInput: Prisma.PostWhereUniqueInput): Promise<Post | null> {
    return this.prisma.post.findUnique({
      where: { ...postWhereUniqueInput, isDeleted: false },
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
    return this.prisma.post.findMany({
      skip,
      take,
      cursor,
      where: { ...where, isDeleted: false },
      orderBy,
    });
  }

  async updatePost(params: { where: Prisma.PostWhereUniqueInput; data: Prisma.PostUpdateInput }) {
    const { data, where } = params;
    return this.prisma.post.update({
      data,
      where,
    });
  }

  async deletePost(where: Prisma.PostWhereUniqueInput): Promise<Post> {
    return this.prisma.post.update({
      data: { isDeleted: true },
      where,
    });
  }
}
