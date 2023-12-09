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
    @SerialName("mimeType")
    val mimeType: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("url_144p")
    val url144P: String? = null,
    @SerialName("url_480p")
    val url480P: String? = null,
    @SerialName("url_720p")
    val url720P: String? = null,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String? = null,
)

enum class BlockType(val type: String) {
    TEXT("text"), MEDIA("media")
}