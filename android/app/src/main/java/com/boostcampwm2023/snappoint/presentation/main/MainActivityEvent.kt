package com.boostcampwm2023.snappoint.presentation.main

sealed class MainActivityEvent {
    data object OpenDrawer: MainActivityEvent()
    data object NavigatePrev: MainActivityEvent()
    data object NavigateClose: MainActivityEvent()
    data class NavigatePreview(val index: Int): MainActivityEvent()
    data class MoveCameraToAddress(val index: Int): MainActivityEvent()
    data object NavigateSignIn: MainActivityEvent()
    data object HalfOpenBottomSheet: MainActivityEvent()
    data object GetAroundPostFailed: MainActivityEvent()
}