package com.boostcampwm2023.snappoint.presentation.main

import com.google.android.gms.maps.model.Marker

data class MainUiState(
    val selectedIndex: Int = -1,    // TODO - MarkerUiState 로 옮기기
    val focusedIndex: Int = -1,     // TODO - MarkerUiState 로 옮기기
    val isPreviewFragmentShowing: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val isLoading: Boolean = false,
)

data class SnapPointState(
    //todo 게시물의 고유 번호로 변경
    val index: Int,
    val markers: List<Marker?>
)
