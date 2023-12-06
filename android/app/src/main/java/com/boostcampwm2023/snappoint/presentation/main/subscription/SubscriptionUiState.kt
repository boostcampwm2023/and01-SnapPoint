package com.boostcampwm2023.snappoint.presentation.main.subscription

import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState

data class SubscriptionUiState(
    val posts: List<PostSummaryState> = emptyList(),
    val onPreviewButtonClicked: (Int) -> Unit,
    val onViewPostButtonClicked: (Int) -> Unit,
)