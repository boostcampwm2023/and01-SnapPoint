package com.boostcampwm2023.snappoint.presentation.auth.signin

sealed class SignInEvent{
    data object NavigateToMainActivity: SignInEvent()
    data class ShowMessage(val errorResId: Int): SignInEvent()
    data object NavigateToSignup: SignInEvent()
}
