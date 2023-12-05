package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import kotlinx.coroutines.flow.Flow

interface RoomRepository {

    fun getLocalPosts(): Flow<List<PostSummaryState>>
    fun getPost(uuid: String): Flow<List<PostSummaryState>>
    suspend fun insertPosts(postSummaryState: PostSummaryState)
    suspend fun deletePost(uuid: String)
}