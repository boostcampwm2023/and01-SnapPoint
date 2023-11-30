package com.boostcampwm2023.snappoint.presentation.signup

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val nickname: String = "",
    val isSignUpInProgress: Boolean = false,
    val isButtonEnabled: Boolean = false,
    val emailCode: Int? = null,
    val passwordCode: Int? = null,
    val passwordConfirmCode: Int? = null,
    val nicknameCode: Int? = null
)