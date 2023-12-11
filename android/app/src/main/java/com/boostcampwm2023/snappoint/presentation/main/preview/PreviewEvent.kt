package com.boostcampwm2023.snappoint.presentation.main.preview

sealed class PreviewEvent {
    data object NavigateViewPost : PreviewEvent()
}