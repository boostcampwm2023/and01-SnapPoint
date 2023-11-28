package com.boostcampwm2023.snappoint.presentation.around

sealed class AroundEvent {
    data class ShowSnapPointAndRoute(val index: Int): AroundEvent()
    data class NavigateViewPost(val index: Int): AroundEvent()
}