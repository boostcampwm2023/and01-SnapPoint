package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getImage(uri: String): Flow<ByteArray>
    fun getImageUri(image: ByteArray): Flow<Unit>
    fun getVideo(uri: String): Flow<ByteArray>
    fun getVideoUri(video: ByteArray): Flow<Unit>
    fun postCreatePost(title: String, postBlocks: List<PostBlockState>): Flow<Unit>
}