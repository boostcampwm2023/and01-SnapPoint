package com.boostcampwm2023.snappoint.presentation.main

data class MainUiState(
    val isClusterPreviewShowing: Boolean = false,
    val isPreviewFragmentShowing: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val isSubscriptionFragmentShowing: Boolean = false,
)
