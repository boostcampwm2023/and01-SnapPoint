package com.boostcampwm2023.snappoint.presentation.login

sealed class LoginResult{
    data class Success(val token: String)
    data class Fail(val error: Int)
}
