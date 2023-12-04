package com.boostcampwm2023.snappoint.presentation.main.around

import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState

data class AroundUiState(
    val posts: List<PostSummaryState> = emptyList(),
    val onPreviewButtonClicked: (Int) -> Unit,
    val onViewPostButtonClicked: (Int) -> Unit,
)

