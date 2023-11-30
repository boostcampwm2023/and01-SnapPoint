package com.boostcampwm2023.snappoint.presentation.signin

sealed class SigninEvent{
    data object Success: SigninEvent()
    data class Fail(val error: Int): SigninEvent()
    data object Signup: SigninEvent()
}
