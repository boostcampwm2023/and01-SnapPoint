package com.boostcampwm2023.snappoint.presentation.signup

sealed class SignupEvent {
    data object Success : SignupEvent()
    data class Fail(val toast: Int) : SignupEvent()
}