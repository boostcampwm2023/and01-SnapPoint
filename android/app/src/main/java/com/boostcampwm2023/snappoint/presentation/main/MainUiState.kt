package com.boostcampwm2023.snappoint.presentation.main

data class MainUiState(
    val isPreviewFragmentShowing: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val isLoading: Boolean = false,
)
