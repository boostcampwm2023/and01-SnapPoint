package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.data.remote.model.response.CreatePostResponse
import com.boostcampwm2023.snappoint.presentation.model.PostBlockCreationState
import com.boostcampwm2023.snappoint.presentation.model.PostCreationState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getImage(uri: String): Flow<ByteArray>
    fun getImageUri(image: ByteArray): Flow<Unit>
    fun getVideo(uri: String): Flow<ByteArray>
    fun getVideoUri(video: ByteArray): Flow<Unit>
    fun postCreatePost(title: String, postBlocks: List<PostBlockCreationState>): Flow<CreatePostResponse>
    fun putModifiedPost(uuid: String, title: String, postBlocks: List<PostBlockCreationState>): Flow<CreatePostResponse>
    fun getAroundPost(leftBottom: String, rightTop: String): Flow<List<PostSummaryState>>
    fun getPost(uuid: String): Flow<PostSummaryState>
    fun deletePost(uuid: String): Flow<PostSummaryState>
}