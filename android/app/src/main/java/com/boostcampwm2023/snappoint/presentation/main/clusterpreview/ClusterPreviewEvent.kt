package com.boostcampwm2023.snappoint.presentation.main.clusterpreview

sealed class ClusterPreviewEvent {
    data class NavigateClusterImage(val index: Int): ClusterPreviewEvent()
}