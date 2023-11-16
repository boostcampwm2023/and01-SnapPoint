package com.boostcampwm2023.snappoint.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CreatePostRequest(
    @SerialName("user_email")
    val userEmail: String,
    @SerialName("title")
    val title: String,
    @SerialName("blocks")
    val blocks: List<Block>
)

@Serializable
data class Block(
    @SerialName("type")
    val type: BlockType,
    @SerialName("content")
    val content: String,
    @SerialName("file_content")
    val fileContent: String?,
    @SerialName("position")
    val position: Position?

)

@Serializable
data class Position (
    @SerialName("x")
    val x: Double,
    @SerialName("y")
    val y:Double
)

enum class BlockType {
    STRING, IMAGE, VIDEO
}
