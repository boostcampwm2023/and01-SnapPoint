package com.boostcampwm2023.snappoint.presentation.viewpost.post

import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

data class PostUiState(
    val title: String = "",
    val author: String = "",
    val timestamp: String = "",
    val posts: List<PostBlockState> = emptyList()
)