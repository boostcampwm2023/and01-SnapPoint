package com.boostcampwm2023.snappoint.presentation.login

data class LoginFormState(
    val isUsernameValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val isLoginInProgress: Boolean = false
)