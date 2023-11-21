package com.boostcampwm2023.snappoint.presentation.main

import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.google.android.gms.maps.model.MarkerOptions

data class MainUiState(
    val posts: List<PostSummaryState> = emptyList(),
    val snapPoints: List<SnapPointState> = emptyList(),
    val isPreviewFragmentShowing: Boolean = false,
    val selectedIndex: Int = -1,
)

data class SnapPointState(
    //todo 게시물의 고유 번호로 변경
    val index: Int,
    val markerOptions: List<MarkerOptions>
)
