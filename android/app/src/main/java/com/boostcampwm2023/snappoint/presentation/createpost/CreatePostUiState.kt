package com.boostcampwm2023.snappoint.presentation.createpost

import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostState

data class CreatePostUiState(
    val title: String = "",
    val postBlocks: List<PostBlockState> = mutableListOf(),
    val isLoading: Boolean = false,
    val blockItemEvent: BlockItemEventListener,
)

data class CreatePostState(
    val title: String = "",
    val postBlocks: List<PostState> = mutableListOf(),
)
