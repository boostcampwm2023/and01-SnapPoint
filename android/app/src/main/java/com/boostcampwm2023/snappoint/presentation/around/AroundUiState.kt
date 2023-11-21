package com.boostcampwm2023.snappoint.presentation.around

data class AroundUiState(
    val posts: List<PostState> = emptyList(),
    val onExpandButtonClick: (Int) -> Unit,
)

data class PostState(
    val title: String,
    val author: String,
    val timeStamp: String,
    val body: String,
    val isExpanded: Boolean = false,
)
