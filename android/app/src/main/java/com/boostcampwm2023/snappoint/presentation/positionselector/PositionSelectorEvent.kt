package com.boostcampwm2023.snappoint.presentation.positionselector

sealed class PositionSelectorEvent {
    data class MoveCameraToAddress(val index: Int): PositionSelectorEvent()
}