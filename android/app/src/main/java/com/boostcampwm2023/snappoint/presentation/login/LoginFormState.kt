package com.boostcampwm2023.snappoint.presentation.login

data class LoginFormState(
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val isLoginInProgress: Boolean = false
)