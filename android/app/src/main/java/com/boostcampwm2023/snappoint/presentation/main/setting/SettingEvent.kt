package com.boostcampwm2023.snappoint.presentation.main.setting

sealed class SettingEvent {
    data object RemoveSnapPoint : SettingEvent()
    data object SignOut : SettingEvent()
    data object FailToSignOut: SettingEvent()
}