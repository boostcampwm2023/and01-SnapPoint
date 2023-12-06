package com.boostcampwm2023.snappoint.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import kotlinx.serialization.Serializable

@Entity(tableName = "postTable")
@Serializable
data class SerializedPost(
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    val uuid: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "post")
    val post: PostSummaryState
)
