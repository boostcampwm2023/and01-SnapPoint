package com.boostcampwm2023.snappoint.data.repository

import android.util.Log
import com.boostcampwm2023.snappoint.data.mapper.asPostBlock
import com.boostcampwm2023.snappoint.data.remote.SnapPointApi
import com.boostcampwm2023.snappoint.data.remote.model.File
import com.boostcampwm2023.snappoint.data.remote.model.request.CreatePostRequest
import com.boostcampwm2023.snappoint.data.remote.model.response.CreatePostResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.PostImageResponse
import com.boostcampwm2023.snappoint.presentation.model.PostState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val snapPointApi: SnapPointApi,
): PostRepository {
    override fun getImage(uri: String): Flow<ByteArray> {
        return flowOf(true)
            .map{
            byteArrayOf()
        }
    }

    override fun getImageUri(image: ByteArray): Flow<Unit> {
        return flowOf(true)
            .map{

            }
    }

    override fun getVideo(uri: String): Flow<ByteArray> {
        return flowOf(true)
            .map{
                byteArrayOf()
            }
    }

    override fun getVideoUri(video: ByteArray): Flow<Unit> {
        return flowOf(true)
            .map{

            }
    }

    override fun postCreatePost(title: String, postBlocks: List<PostState>): Flow<CreatePostResponse> {

        println("postCreatePost started..")
        println("postBlocks: $postBlocks")
        val request = CreatePostRequest(
            title = title,
            postBlocks = postBlocks.map {
                when (it) {
                    is PostState.IMAGE -> {
                        val requestBody =
                            it.imageByteArray.toRequestBody("image/webp".toMediaTypeOrNull())
                        val multipartBody =
                            MultipartBody.Part.createFormData("file", "image", requestBody)
                        val uploadResult = try {
                            runBlocking(Dispatchers.IO) {
                                snapPointApi.postImage(multipartBody)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            return emptyFlow()
                        }

                        // TODO - 하나의 이미지 블럭에 사진이 여러개 들어갈 때 대응
                        it.asPostBlock().copy(
                            files = listOf(File(uploadResult.body()?.uuid ?: ""))
                        )
                    }
                    else -> it.asPostBlock()
                }
            }
        )
        Log.d("TAG", "postCreatePost: ${Json.encodeToString(request)}")

        return flowOf(true)
            .map{
                snapPointApi.createPost(request)
            }
    }

    override suspend fun postImage(byteArray: ByteArray): Response<PostImageResponse> {

        val requestBody = byteArray.toRequestBody("image/webp".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", "image", requestBody)

        return snapPointApi.postImage(multipartBody)
    }
}