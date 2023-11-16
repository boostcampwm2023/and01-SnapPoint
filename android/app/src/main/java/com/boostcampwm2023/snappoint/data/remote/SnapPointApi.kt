package com.boostcampwm2023.snappoint.data.remote

import com.boostcampwm2023.snappoint.data.remote.model.request.CreatePostRequest
import com.boostcampwm2023.snappoint.data.remote.model.response.CreatePostResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.ImageResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.ImageUriResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SnapPointApi {

    @GET("image")
    suspend fun getImage(
       @Query ("uri") uri: String,
    ): ImageResponse

    @GET("image_uri")
    suspend fun getImageUri(
        @Query ("image") image: String,
    ): ImageUriResponse

    @POST("create_post")
    suspend fun createPost(
        @Body createPostRequest: CreatePostRequest,
    ): CreatePostResponse

}