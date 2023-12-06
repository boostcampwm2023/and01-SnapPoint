package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.data.local.dao.PostDao
import com.boostcampwm2023.snappoint.data.local.entity.SerializedPost
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val localPostDao: PostDao
) : RoomRepository {

    override fun getLocalPosts(email: String): Flow<List<PostSummaryState>> {
        return localPostDao.getAllPosts(email)
            .map { serializedPosts ->
                serializedPosts.map { serializedPost ->
                    serializedPost.post
                }
            }
    }

    override fun getPost(uuid: String, email: String): Flow<List<PostSummaryState>> {
        return localPostDao.getPost(uuid, email)
            .map { serializedPosts ->
                serializedPosts.map { serializedPost ->
                    serializedPost.post
                }
            }
    }

    override suspend fun insertPosts(postSummaryState: PostSummaryState, email: String) {
        localPostDao.insertPost(
            SerializedPost(postSummaryState.uuid, email, postSummaryState)
        )
    }

    override suspend fun deletePost(uuid: String, email: String) {
        localPostDao.deletePost(uuid, email)
    }
}