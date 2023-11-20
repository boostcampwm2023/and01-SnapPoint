package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.data.mapper.asPostBlock
import com.boostcampwm2023.snappoint.data.remote.SnapPointApi
import com.boostcampwm2023.snappoint.data.remote.model.request.CreatePostRequest
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

    override fun postCreatePost(title: String, postBlocks: List<PostBlockState>): Flow<Unit> {

        val request = CreatePostRequest(
            userEmail = "wsb7788",
            title = title,
            postBlocks = postBlocks.map {
                it.asPostBlock()
            }
        )

        return flowOf(true)
            .map{
                delay(1000)
                //snapPointApi.createPost(request)
            }
    }
}