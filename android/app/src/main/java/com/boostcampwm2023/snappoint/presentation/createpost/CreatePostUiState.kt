package com.boostcampwm2023.snappoint.presentation.createpost

import android.net.Uri

data class CreatePostUiState(
    val postBlocks: List<PostBlock> = mutableListOf(),
    val onTextChanged: (position: Int, content: String) -> Unit,
    val onDeleteButtonClicked: (position: Int) -> Unit,
)

sealed class PostBlock(open val content: String) {
    data class STRING(override val content: String = "") : PostBlock(content)
    data class IMAGE(override val content: String = "", val uri: Uri, val position: Position) : PostBlock(content)
    data class VIDEO(override val content: String = "", val position: Position) : PostBlock(content)
}

enum class ViewType {
    STRING,
    IMAGE,
    VIDEO,
}

data class Position(
    val latitude: Double,
    val longitude: Double
)
