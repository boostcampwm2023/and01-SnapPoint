package com.boostcampwm2023.snappoint.presentation.createpost

import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

data class CreatePostUiState(
    val title: String = "",
    val postBlocks: List<PostBlockState> = mutableListOf(),
    val isLoading: Boolean = false,
    val blockItemEvent: BlockItemEventListener,
)