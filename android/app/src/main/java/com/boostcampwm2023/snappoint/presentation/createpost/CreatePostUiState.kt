package com.boostcampwm2023.snappoint.presentation.createpost

import com.boostcampwm2023.snappoint.presentation.model.PostBlockCreationState

data class CreatePostUiState(
    val title: String = "",
    val postBlocks: List<PostBlockCreationState> = mutableListOf(),
    val isLoading: Boolean = false,
    val blockItemEvent: BlockItemEventListener,
)