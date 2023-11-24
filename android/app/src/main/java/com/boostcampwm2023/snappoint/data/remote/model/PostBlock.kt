package com.boostcampwm2023.snappoint.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostBlock(
    @SerialName("uuid")
    val blockUuid: String? = null,
    @SerialName("content")
    val content: String,
    @SerialName("type")
    val type: String,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("files")
    val files: List<File>? = null,
)

@Serializable
data class File(
    @SerialName("uuid")
    val fileUuid: String,
    @SerialName("url")
    val url: String? = null,
    @SerialName("mimeType")
    val mimeType: String? = null,
)

enum class BlockType(val type: String) {
    TEXT("text"), MEDIA("media")
}