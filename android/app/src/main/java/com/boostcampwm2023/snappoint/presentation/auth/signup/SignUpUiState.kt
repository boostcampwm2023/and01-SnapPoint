package com.boostcampwm2023.snappoint.presentation.auth.signup

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val nickname: String = "",
    val isSignUpInProgress: Boolean = false,
    val isButtonEnabled: Boolean = false,
    val emailErrorResId: Int? = null,
    val passwordErrorResId: Int? = null,
    val passwordConfirmErrorResId: Int? = null,
    val nicknameErrorResId: Int? = null
)