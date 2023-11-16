package com.boostcampwm2023.snappoint.di

import com.boostcampwm2023.snappoint.data.remote.SnapPointApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideSnapPointApi(): SnapPointApi{
        return Retrofit.Builder()
            .baseUrl("https://asdf.com/")
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(SnapPointApi::class.java)
    }

}