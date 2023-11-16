package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.data.remote.SnapPointApi
import com.boostcampwm2023.snappoint.presentation.createpost.PostBlock
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

    override fun postCreatePost(postBlocks: List<PostBlock>): Flow<Unit> {
        return flowOf(true)
            .map{

            }
    }
}