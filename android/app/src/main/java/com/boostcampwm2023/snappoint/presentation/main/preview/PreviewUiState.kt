package com.boostcampwm2023.snappoint.presentation.main.preview

import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

data class PreviewUiState(
    val uuid: String = "",
    val title: String = "",
    val timeStamp: String = "",
    val blocks: List<PostBlockState> = emptyList()
)