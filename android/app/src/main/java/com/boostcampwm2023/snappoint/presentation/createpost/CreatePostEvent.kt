package com.boostcampwm2023.snappoint.presentation.createpost

sealed class CreatePostEvent {
    data class ShowMessage(val resId: Int): CreatePostEvent()
    data class FindAddress(val index: Int, val position: PositionState) : CreatePostEvent()

    data object SelectImageFromLocal: CreatePostEvent()
    data object NavigatePrev : CreatePostEvent()
}