package com.boostcampwm2023.snappoint.presentation.login

sealed class LoginEvent{
    data class Success(val token: String): LoginEvent()
    data class Fail(val error: Int): LoginEvent()
}
