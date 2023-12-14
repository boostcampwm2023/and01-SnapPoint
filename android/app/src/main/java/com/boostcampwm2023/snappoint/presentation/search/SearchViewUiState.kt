package com.boostcampwm2023.snappoint.presentation.search

data class SearchViewUiState(
    val texts: List<String> = emptyList(),
    val onAutoCompleteItemClicked: (Int) -> Unit = {},
)
