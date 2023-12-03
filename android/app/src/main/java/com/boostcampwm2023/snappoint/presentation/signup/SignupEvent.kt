package com.boostcampwm2023.snappoint.presentation.signup

sealed class SignupEvent {
    data object NavigateToSignIn : SignupEvent()
    data class ShowMessage(val messageResId: Int) : SignupEvent()
}