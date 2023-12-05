package com.boostcampwm2023.snappoint.presentation.viewpost.post

sealed class PostEvent {
    data object NavigatePrev: PostEvent()
    data object SavePost: PostEvent()
}