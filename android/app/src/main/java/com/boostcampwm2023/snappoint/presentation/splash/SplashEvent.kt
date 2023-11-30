package com.boostcampwm2023.snappoint.presentation.splash

sealed class SplashEvent {
    data object Success: SplashEvent()
    data object Fail: SplashEvent()
}