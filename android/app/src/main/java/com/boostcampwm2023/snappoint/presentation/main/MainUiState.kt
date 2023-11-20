package com.boostcampwm2023.snappoint.presentation.main

import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState

data class MainUiState(
    val posts: List<PostSummaryState> = emptyList()
)
