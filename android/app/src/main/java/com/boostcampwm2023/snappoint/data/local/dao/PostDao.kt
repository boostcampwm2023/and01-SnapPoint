package com.boostcampwm2023.snappoint.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boostcampwm2023.snappoint.data.local.entity.SerializedPost
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM postTable")
    fun getAllPosts(): Flow<List<SerializedPost>>

    @Query("SELECT * FROM postTable WHERE uuid == :uuid")
    fun getPost(uuid: String): Flow<List<SerializedPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(vararg posts: SerializedPost)

    @Query("DELETE FROM postTable WHERE uuid == :uuid")
    fun deletePost(uuid: String)
}