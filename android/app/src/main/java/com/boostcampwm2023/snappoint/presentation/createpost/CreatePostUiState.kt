package com.boostcampwm2023.snappoint.presentation.createpost

data class CreatePostUiState(
    val title: String,
    val postBlocks: List<PostBlockState> = mutableListOf(),
    val onTextChanged: (position: Int, content: String) -> Unit,
)

sealed class PostBlockState(open val content: String) {
    data class STRING(override val content: String = "") : PostBlockState(content)
    data class IMAGE(override val content: String = "", val position: PositionState) : PostBlockState(content)
    data class VIDEO(override val content: String = "", val position: PositionState) : PostBlockState(content)
}

data class PositionState(
    val latitude: Double,
    val longitude: Double
)
