package com.boostcampwm2023.snappoint.presentation.preview

import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

data class PreviewUiState(
    val title: String = "",
    val timeStamp: String = "",
    val blocks: List<PostBlockState> = emptyList()
)