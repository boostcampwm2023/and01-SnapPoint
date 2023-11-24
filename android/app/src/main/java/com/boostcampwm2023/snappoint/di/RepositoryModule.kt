package com.boostcampwm2023.snappoint.di

import com.boostcampwm2023.snappoint.data.repository.PostRepository
import com.boostcampwm2023.snappoint.data.repository.PostRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryImplModule{

    @Binds
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository
}