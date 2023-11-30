package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.data.remote.model.response.CreatePostResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.PostImageResponse
import com.boostcampwm2023.snappoint.presentation.model.PostState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface PostRepository {
    fun getImage(uri: String): Flow<ByteArray>
    fun getImageUri(image: ByteArray): Flow<Unit>
    fun getVideo(uri: String): Flow<ByteArray>
    fun getVideoUri(video: ByteArray): Flow<Unit>
    fun postCreatePost(title: String, postBlocks: List<PostState>): Flow<CreatePostResponse>
    fun postImage(byteArray: ByteArray): Flow<PostImageResponse>
}