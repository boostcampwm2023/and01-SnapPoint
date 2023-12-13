package com.boostcampwm2023.snappoint.presentation.auth.signup

sealed class SignUpEvent {
    data object NavigateToSignIn : SignUpEvent()
    data class ShowMessage(val messageResId: Int) : SignUpEvent()
}