package com.boostcampwm2023.snappoint.presentation.main.subscription

sealed class SubscriptionEvent {
    data class ShowSnapPointAndRoute(val index: Int): SubscriptionEvent()
    data class NavigateViewPost(val index: Int): SubscriptionEvent()
}