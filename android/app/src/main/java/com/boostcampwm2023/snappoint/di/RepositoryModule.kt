package com.boostcampwm2023.snappoint.di

import com.boostcampwm2023.snappoint.data.local.dao.PostDao
import com.boostcampwm2023.snappoint.data.repository.SignInRepository
import com.boostcampwm2023.snappoint.data.repository.SignInRepositoryImpl
import com.boostcampwm2023.snappoint.data.repository.PostRepository
import com.boostcampwm2023.snappoint.data.repository.PostRepositoryImpl
import com.boostcampwm2023.snappoint.data.repository.RoomRepository
import com.boostcampwm2023.snappoint.data.repository.RoomRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRoomRepository(postDao: PostDao): RoomRepository {
        return RoomRepositoryImpl(postDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryImplModule{

    @Binds
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    abstract fun bindLoginRepository(impl: SignInRepositoryImpl): SignInRepository
}