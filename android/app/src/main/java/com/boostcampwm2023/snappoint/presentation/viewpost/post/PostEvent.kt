package com.boostcampwm2023.snappoint.presentation.viewpost.post

sealed class PostEvent {
    data object navigatePrev: PostEvent()
}