package com.boostcampwm2023.snappoint.presentation.signup

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val nickname: String = "",
    val isSignUpInProgress: Boolean = false
)