package com.boostcampwm2023.snappoint.presentation.viewpost.post

import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState

data class PostUiState(
    val posts: List<PostSummaryState> = emptyList()
)