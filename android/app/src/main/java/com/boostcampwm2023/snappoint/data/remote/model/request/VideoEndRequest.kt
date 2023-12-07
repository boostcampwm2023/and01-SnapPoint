package com.boostcampwm2023.snappoint.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoEndRequest(
    @SerialName("key")
    val key: String,
    @SerialName("uploadId")
    val uploadId: String,
    @SerialName("mimeType")
    val mimeType: String,
    @SerialName("parts")
    val parts: List<Part>,
)

@Serializable
data class Part(
    @SerialName("PartNumber")
    val partNumber: Int,
    @SerialName("ETag")
    val eTag: String,
)
