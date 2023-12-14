package com.boostcampwm2023.snappoint.presentation.positionselector

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.search.SearchViewUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PositionSelectorViewModel @Inject constructor() : ViewModel() {

    private val _searchViewUiState: MutableStateFlow<SearchViewUiState> = MutableStateFlow(
        SearchViewUiState(onAutoCompleteItemClicked = { index ->
            moveCameraToAddress(index)
        })
    )
    val searchViewUiState: StateFlow<SearchViewUiState> = _searchViewUiState.asStateFlow()

    fun updateAutoCompleteTexts(texts: List<String>) {
        _searchViewUiState.update {
            it.copy(texts = texts)
        }
    }

    private val _event: MutableSharedFlow<PositionSelectorEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<PositionSelectorEvent> = _event.asSharedFlow()

    private fun moveCameraToAddress(index: Int) {
        _event.tryEmit(PositionSelectorEvent.MoveCameraToAddress(index))
    }
}