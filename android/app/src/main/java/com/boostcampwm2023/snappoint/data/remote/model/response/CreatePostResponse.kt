package com.boostcampwm2023.snappoint.data.remote.model.response

import com.boostcampwm2023.snappoint.data.remote.model.PostBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CreatePostResponse(
    @SerialName("uuid")
    val postUuid: String,
    @SerialName("title")
    val title: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("modifiedAt")
    val modifiedAt: String,
    @SerialName("summary")
    val summary: String,
    @SerialName("isPublished")
    val isPublished: Boolean = true,
    @SerialName("blocks")
    val blocks: List<PostBlock>
)