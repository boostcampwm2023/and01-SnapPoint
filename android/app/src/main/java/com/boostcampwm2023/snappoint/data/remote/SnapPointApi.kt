package com.boostcampwm2023.snappoint.data.remote

import com.boostcampwm2023.snappoint.data.remote.model.request.CreatePostRequest
import com.boostcampwm2023.snappoint.data.remote.model.response.CreatePostResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.ImageResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.ImageUriResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.PostImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @POST("posts/publish")
    suspend fun createPost(
        @Body createPostRequest: CreatePostRequest,
    ): CreatePostResponse

    @Multipart
    @POST("files")
    suspend fun postImage(
        @Part bitmap: MultipartBody.Part?
    ): Call<PostImageResponse>
}