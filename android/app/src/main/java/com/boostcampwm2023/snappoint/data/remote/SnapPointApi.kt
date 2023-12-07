package com.boostcampwm2023.snappoint.data.remote

import com.boostcampwm2023.snappoint.data.remote.model.request.CreatePostRequest
import com.boostcampwm2023.snappoint.data.remote.model.request.SignInRequest
import com.boostcampwm2023.snappoint.data.remote.model.request.SignupRequest
import com.boostcampwm2023.snappoint.data.remote.model.request.VideoAbortRequest
import com.boostcampwm2023.snappoint.data.remote.model.request.VideoEndRequest
import com.boostcampwm2023.snappoint.data.remote.model.request.VideoUrlRequest
import com.boostcampwm2023.snappoint.data.remote.model.response.CreatePostResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.GetPostResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.VideoStartResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.ImageResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.ImageUriResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.PostImageResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.SignInResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.SignupResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.VideoEndResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.VideoUrlResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

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

    @POST("signin")
    suspend fun postSignIn(
        @Body loginRequest: SignInRequest
    ): SignInResponse

    @GET("logout")
    suspend fun getSignOut()

    @POST("signup")
    suspend fun postSignUp(
        @Body signupRequest: SignupRequest
    ): SignupResponse

    @Multipart
    @POST("files")
    suspend fun postImage(
        @Part bitmap: MultipartBody.Part
    ): PostImageResponse

    @GET("posts")
    suspend fun getAroundPost(
        @Query("from") leftBottom: String,
        @Query("to") rightTop: String
    ): List<GetPostResponse>

    @GET("posts/{uuid}")
    suspend fun getPost(
        @Path("uuid") uuid: String
    ): GetPostResponse

    @GET("files/video-start")
    suspend fun getVideoStart(
        @Query("contentType") contentType: String,
    ): VideoStartResponse

    @POST("files/video-url")
    suspend fun postVideoUrl(
        @Body videoUrlRequest: VideoUrlRequest,
    ): VideoUrlResponse

    @POST("files/video-end")
    suspend fun postVideoEnd(
        @Body videoEndRequest: VideoEndRequest,
    ): VideoEndResponse

    @POST("files/video-abort")
    suspend fun postVideoAbort(
        @Body videoAbortRequest: VideoAbortRequest
    ): Unit

    @Multipart
    @PUT
    suspend fun putVideo(
        @Url url: String,
        @Part body: MultipartBody.Part,
    ): Response<Unit>

}