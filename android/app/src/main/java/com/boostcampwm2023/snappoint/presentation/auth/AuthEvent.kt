package com.boostcampwm2023.snappoint.presentation.auth

sealed class AuthEvent {
    data object GoogleSignIn : AuthEvent()
}