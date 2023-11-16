package com.boostcampwm2023.snappoint.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostBlock(
    @SerialName("type")
    val type: BlockType,
    @SerialName("content")
    val content: String,
    @SerialName("file_content")
    val fileContent: String? = null,
    @SerialName("position")
    val position: Position? = null
)

@Serializable
data class Position (
    @SerialName("x")
    val x: Double,
    @SerialName("y")
    val y:Double
)

enum class BlockType(val type: String) {
    TEXT("Text"), IMAGE("Image"), VIDEO("Video")
}