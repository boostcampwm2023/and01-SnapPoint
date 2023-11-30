package com.boostcampwm2023.snappoint.presentation.signin

data class SigninFormState(
    val email: String = "",
    val password: String = "",
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val isLoginInProgress: Boolean = false
)