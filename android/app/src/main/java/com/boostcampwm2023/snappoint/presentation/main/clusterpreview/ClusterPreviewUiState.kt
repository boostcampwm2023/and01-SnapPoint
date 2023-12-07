package com.boostcampwm2023.snappoint.presentation.main.clusterpreview

import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

data class ClusterPreviewUiState(
    val clusters: List<PostBlockState> = emptyList(),
    val onItemClicked: (Int) -> Unit,
)
