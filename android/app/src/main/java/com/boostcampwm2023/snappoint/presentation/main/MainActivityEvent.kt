package com.boostcampwm2023.snappoint.presentation.main

sealed class MainActivityEvent {
    data object OpenDrawer: MainActivityEvent()
}