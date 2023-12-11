package com.boostcampwm2023.snappoint.presentation.videoedit

sealed class VideoEditEvent {
    data object OnPlayButtonClicked: VideoEditEvent()
    data object StopPlayer : VideoEditEvent()
}