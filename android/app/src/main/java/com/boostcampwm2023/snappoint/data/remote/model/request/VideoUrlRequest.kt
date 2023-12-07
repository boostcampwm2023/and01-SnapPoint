package com.boostcampwm2023.snappoint.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoUrlRequest(
    @SerialName("key")
    val key: String,
    @SerialName("uploadId")
    val uploadId: String,
    @SerialName("partNumber")
    val partNumber: Int,
)

