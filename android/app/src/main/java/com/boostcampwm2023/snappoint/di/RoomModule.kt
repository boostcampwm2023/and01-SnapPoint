package com.boostcampwm2023.snappoint.di

import android.content.Context
import androidx.room.Room
import com.boostcampwm2023.snappoint.data.local.PostDatabase
import com.boostcampwm2023.snappoint.data.local.dao.PostDao
import com.boostcampwm2023.snappoint.data.repository.RoomRepository
import com.boostcampwm2023.snappoint.data.repository.RoomRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun providePostDatabase(@ApplicationContext context: Context): PostDatabase {
        return Room.databaseBuilder(
            context,
            PostDatabase::class.java,
            "database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePostDao(postDatabase: PostDatabase): PostDao {
        return postDatabase.getPostDao()
    }

    @Provides
    @Singleton
    fun provideRoomRepository(postDao: PostDao): RoomRepository {
        return RoomRepositoryImpl(postDao)
    }
}