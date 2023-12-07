package com.boostcampwm2023.snappoint.data.repository

import android.util.Log
import androidx.core.net.toFile
import com.boostcampwm2023.snappoint.data.mapper.asPostBlock
import com.boostcampwm2023.snappoint.data.mapper.asPostSummaryState
import com.boostcampwm2023.snappoint.data.remote.SnapPointApi
import com.boostcampwm2023.snappoint.data.remote.model.File
import com.boostcampwm2023.snappoint.data.remote.model.request.CreatePostRequest
import com.boostcampwm2023.snappoint.data.remote.model.request.Part
import com.boostcampwm2023.snappoint.data.remote.model.request.VideoEndRequest
import com.boostcampwm2023.snappoint.data.remote.model.request.VideoUrlRequest
import com.boostcampwm2023.snappoint.data.remote.model.response.CreatePostResponse
import com.boostcampwm2023.snappoint.presentation.model.PostBlockCreationState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.util.toByteArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import javax.inject.Inject
import kotlin.math.min

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

    override fun postCreatePost(title: String, postBlocks: List<PostBlockCreationState>): Flow<CreatePostResponse> {

        return flowOf(true)
            .map{
                CreatePostRequest(
                    title = title,
                    postBlocks = postBlocks.map {
                        when (it) {
                            is PostBlockCreationState.IMAGE -> {
                                val uuid = uploadImageAndGetUUid(it)
                                it.asPostBlock().copy(
                                    files = listOf(File(uuid))
                                )
                            }
                            is PostBlockCreationState.VIDEO -> {
                                val uuid = uploadVideoAndGetUUid(it)
                                it.asPostBlock().copy(
                                    files = listOf(File(uuid))
                                )
                            }
                            else -> it.asPostBlock()
                        }
                    }
                )
            }.map{request ->
                snapPointApi.createPost(request)
            }
    }

    private suspend fun uploadVideoAndGetUUid(videoBlock: PostBlockCreationState.VIDEO): String {

        val videoStartResponse = snapPointApi.getVideoStart(contentType = videoBlock.mimeType)
        val (key, uploadId) = videoStartResponse

        val file = java.io.File("/storage/emulated/0/Android/data/com.boostcampwm2023.snappoint/cache/123.mp4")
        //val file = java.io.File(videoBlock.resultPath)

        val fileByteArray = file.readBytes()
        val parts = mutableListOf<Part>()
        val MB5 = 1024*1024*5

        for(partNumber in 0 until fileByteArray.size step MB5){
            val postVideoUrlResponse = snapPointApi.postVideoUrl(
                videoUrlRequest = VideoUrlRequest(
                    key = key,
                    uploadId = uploadId,
                    partNumber = parts.size + 1
                )
            )
            val preSignedUrl = postVideoUrlResponse.preSignedUrl
            val body = fileByteArray.toRequestBody(
                contentType = videoBlock.mimeType.toMediaType(),
                offset = partNumber,
                byteCount = min(MB5, fileByteArray.size - partNumber)
            )
            val multipartBody = MultipartBody.Part.create(body)
            val response = snapPointApi.putVideo(
                url = preSignedUrl,
                body = multipartBody
            )
            val eTag = response.headers().get("ETag")?: ""
            Log.d("TAG", "eTag: $eTag")
            parts.add(Part(parts.size + 1, eTag.trim('"')))
        }

        val res = snapPointApi.postVideoEnd(
            VideoEndRequest(
                key = key,
                uploadId = uploadId,
                mimeType = videoBlock.mimeType,
                parts = parts
            )
        )
        return res.uuid
    }

    private suspend fun uploadImageAndGetUUid(imageBlock: PostBlockCreationState.IMAGE): String {
        val requestBody = imageBlock.bitmap?.toByteArray()?.toRequestBody("image/webp".toMediaType())!!
        val multipartBody = MultipartBody.Part.createFormData("file", "image", requestBody)
        val uploadResult = snapPointApi.postImage(multipartBody)
        return uploadResult.uuid
    }

    override fun getAroundPost(leftBottom: String, rightTop: String): Flow<List<PostSummaryState>> {

        return flowOf(true
        ).map {
            snapPointApi.getAroundPost(leftBottom, rightTop)
        }.map{ response ->
            response.asPostSummaryState()
        }
    }

    override fun getPost(uuid: String): Flow<PostSummaryState> {

        return flowOf(true)
            .map {
                snapPointApi.getPost(uuid).asPostSummaryState()
            }
    }
}