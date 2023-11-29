package com.boostcampwm2023.snappoint.presentation.login

sealed class LoginEvent{
    data object Success: LoginEvent()
    data class Fail(val error: Int): LoginEvent()
}
