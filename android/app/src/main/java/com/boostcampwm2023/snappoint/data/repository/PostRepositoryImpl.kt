package com.boostcampwm2023.snappoint.data.repository

import android.util.Log
import com.boostcampwm2023.snappoint.data.mapper.asPostBlock
import com.boostcampwm2023.snappoint.data.mapper.asPostSummaryState
import com.boostcampwm2023.snappoint.data.remote.SnapPointApi
import com.boostcampwm2023.snappoint.data.remote.model.File
import com.boostcampwm2023.snappoint.data.remote.model.PostBlock
import com.boostcampwm2023.snappoint.data.remote.model.request.CreatePostRequest
import com.boostcampwm2023.snappoint.data.remote.model.response.CreatePostResponse
import com.boostcampwm2023.snappoint.presentation.model.PostBlockCreationState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.util.toByteArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    override fun postCreatePost(title: String, postBlocks: List<PostBlockCreationState>): Flow<CreatePostResponse> {

        return flowOf(true)
            .map {
                val request = buildCreatePostRequest(title, postBlocks)
                val response = snapPointApi.createPost(request)
                response
            }
    }

    override fun putModifiedPost(uuid: String, title: String, postBlocks: List<PostBlockCreationState>): Flow<CreatePostResponse> {

        return flowOf(true)
            .map {
                val request = buildCreatePostRequest(title, postBlocks)
                val response = snapPointApi.modifyPost(uuid, request)
                response
            }
    }

    override fun getAroundPost(leftBottom: String, rightTop: String): Flow<List<PostSummaryState>> {

        return flowOf(true)
            .map {
                snapPointApi.getAroundPost(leftBottom, rightTop)
            }.map { response ->
                response.asPostSummaryState()
            }
    }

    override fun getPost(uuid: String): Flow<PostSummaryState> {

        return flowOf(true)
            .map {
                val response = snapPointApi.getPost(uuid)
                response.asPostSummaryState()
            }
    }

    private suspend fun buildCreatePostRequest(title: String, postBlockStates: List<PostBlockCreationState>): CreatePostRequest {

        val postBlocks: List<PostBlock> = postBlockStates.map {
            when (it) {
                is PostBlockCreationState.IMAGE -> {
                    if (it.uuid.isBlank()) {
                        it.asPostBlock().copy(
                            files = listOf(File(getImageUuid(it)))
                        )
                    } else {
                        it.asPostBlock().copy(
                            files = listOf(File(it.fileUuid))
                        )
                    }
                }

                else -> it.asPostBlock()
            }
        }

        return CreatePostRequest(title = title, postBlocks = postBlocks)
    }

    private suspend fun getImageUuid(block: PostBlockCreationState.IMAGE): String {
        val requestBody = block.bitmap?.toByteArray()?.toRequestBody("image/webp".toMediaType())!!
        val multipartBody = MultipartBody.Part.createFormData("file", "image", requestBody)
        val uploadResult = snapPointApi.postImage(multipartBody)

        return uploadResult.uuid
    }
}