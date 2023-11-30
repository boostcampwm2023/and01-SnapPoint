package com.boostcampwm2023.snappoint.presentation.signin

sealed class SignInEvent{
    data object Success: SignInEvent()
    data class Fail(val error: Int): SignInEvent()
    data object Signup: SignInEvent()
}
