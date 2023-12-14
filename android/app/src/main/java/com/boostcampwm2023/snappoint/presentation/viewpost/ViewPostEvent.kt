package com.boostcampwm2023.snappoint.presentation.viewpost

sealed class ViewPostEvent {
    data object FinishActivity: ViewPostEvent()
    data object SuccessToDeletePost: ViewPostEvent()
    data object FailToDeletePost: ViewPostEvent()
}