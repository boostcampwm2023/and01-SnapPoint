package com.boostcampwm2023.snappoint.presentation.viewpost.post

sealed class PostEvent {
    data object NavigatePrev: PostEvent()
    data object SavePost: PostEvent()
    data object DeletePost: PostEvent()
    data class MenuItemClicked(val itemId: Int): PostEvent()
}