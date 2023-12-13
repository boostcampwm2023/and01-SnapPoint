package com.boostcampwm2023.snappoint.presentation.auth

data class AuthUiState(
    val fragmentHeight: Int = Int.MAX_VALUE,
    val handleHeight: Int = 0,
    val bottomSheetHeight: Int = 1,
    val isBottomSheetActivated: Boolean = false
)