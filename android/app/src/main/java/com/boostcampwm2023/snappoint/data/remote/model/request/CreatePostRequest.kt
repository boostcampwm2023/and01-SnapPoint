package com.boostcampwm2023.snappoint.data.remote.model.request

import com.boostcampwm2023.snappoint.data.remote.model.PostBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CreatePostRequest(
    @SerialName("title")
    val title: String,
    @SerialName("blocks")
    val postBlocks: List<PostBlock>
)


