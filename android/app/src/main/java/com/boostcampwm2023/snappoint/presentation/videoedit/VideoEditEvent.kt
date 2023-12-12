package com.boostcampwm2023.snappoint.presentation.videoedit

sealed class VideoEditEvent {
    data object OnPlayButtonClicked: VideoEditEvent()
    data object StopPlayer : VideoEditEvent()
    data object OnBackButtonClicked: VideoEditEvent()
    data object OnUploadWithoutEditButtonClicked: VideoEditEvent()
    data object OnCheckButtonClicked: VideoEditEvent()
}