package com.boostcampwm2023.snappoint.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostImageResponse(
    @SerialName("uuid")
    val uuid: String,
    @SerialName("url")
    val url: String,
    @SerialName("mimeType")
    val mimeType: String,
)
