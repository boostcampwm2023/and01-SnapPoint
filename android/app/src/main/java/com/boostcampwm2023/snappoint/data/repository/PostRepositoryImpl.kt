package com.boostcampwm2023.snappoint.data.repository

import android.util.Base64
import android.util.Log
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
import com.boostcampwm2023.snappoint.presentation.util.Constants.BYTE_OF_VIDEO_PART_SIZE
import com.boostcampwm2023.snappoint.presentation.util.toByteArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
                                Log.d("TAG", "postCreatePost: $uuid")
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

        val file = java.io.File(videoBlock.uri.toString())

        val fileByteArray = file.readBytes()

        val byteOfFileSize = fileByteArray.size
        val parts = mutableListOf<Part>()

        for(partNumber in 0 until fileByteArray.size step BYTE_OF_VIDEO_PART_SIZE){
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
                byteCount = min(BYTE_OF_VIDEO_PART_SIZE, byteOfFileSize - partNumber)
            )
            val response = snapPointApi.putVideo(
                url = preSignedUrl,
                body = body
            )
            val eTag = response.headers()["ETag"] ?: throw Exception("서버 업로드 실패")
            parts.add(Part(parts.size + 1, eTag.trim('"')))
        }

        val videoEndResponse = snapPointApi.postVideoEnd(
            VideoEndRequest(
                key = key,
                uploadId = uploadId,
                mimeType = videoBlock.mimeType,
                parts = parts
            )
        )
        Log.d("TAG", "uploadVideoAndGetUUid: ${videoEndResponse}")
        return videoEndResponse.uuid
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