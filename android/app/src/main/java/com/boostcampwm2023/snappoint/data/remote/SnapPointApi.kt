package com.boostcampwm2023.snappoint.data.remote

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface SnapPointApi {

    @GET("image")
    suspend fun getImage(
       @Query ("uri") uri: String,
    ): String

}