package com.boostcampwm2023.snappoint.presentation.signup

data class SignupUiState(
    private val email: String = "",
    private val password: String = "",
    private val passwordConfirm: String = "",
    private val nickname: String = ""
)