package com.boostcampwm2023.snappoint.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoStartResponse(
    @SerialName("key")
    val key: String,
    @SerialName("uploadId")
    val uploadId: String,
)
