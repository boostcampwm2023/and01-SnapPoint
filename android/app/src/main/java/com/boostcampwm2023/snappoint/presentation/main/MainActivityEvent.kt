package com.boostcampwm2023.snappoint.presentation.main

sealed class MainActivityEvent {
    data object OpenDrawer: MainActivityEvent()
    data object NavigatePrev: MainActivityEvent()
    data object NavigateClose: MainActivityEvent()
    data class NavigatePreview(val index: Int): MainActivityEvent()
}