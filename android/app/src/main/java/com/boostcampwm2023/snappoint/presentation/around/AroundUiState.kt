package com.boostcampwm2023.snappoint.presentation.around

data class AroundUiState(
    val posts: List<PostState> = emptyList(),
)

data class PostState(
    val title: String,
    val author: String,
    val timeStamp: String,
    val body: String,
)
