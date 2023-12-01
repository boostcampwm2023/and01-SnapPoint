package com.boostcampwm2023.snappoint.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.data.mapper.asPostSummaryState
import com.boostcampwm2023.snappoint.data.repository.PostRepository
import com.boostcampwm2023.snappoint.presentation.main.search.SearchViewUiState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.model.SnapPointTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val postRepository: PostRepository
) :ViewModel(){

    private val _postState: MutableStateFlow<List<PostSummaryState>> = MutableStateFlow(emptyList())
    val postState: StateFlow<List<PostSummaryState>> = _postState.asStateFlow()

    private val _uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _searchViewUiState: MutableStateFlow<SearchViewUiState> = MutableStateFlow(
        SearchViewUiState(onAutoCompleteItemClicked = { index ->
            moveCameraToAddress(index)
        })
    )
    val searchViewUiState: StateFlow<SearchViewUiState> = _searchViewUiState.asStateFlow()

    private val _event: MutableSharedFlow<MainActivityEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<MainActivityEvent> = _event.asSharedFlow()

    var bottomSheetHeight: Int = 0

    fun loadPosts(leftBottom: String, rightTop: String) {

        postRepository.getAroundPost(leftBottom, rightTop)
            .onStart { _uiState.update { it.copy(isLoading = true) } }
            .catch { _event.tryEmit(MainActivityEvent.GetAroundPostFailed) }
            .onCompletion { _uiState.update { it.copy(isLoading = false) } }
            .onEach { response ->
                _postState.value = response.map { it.asPostSummaryState() }
                _event.tryEmit(MainActivityEvent.HalfOpenBottomSheet)
            }.launchIn(viewModelScope)
    }

    // TODO DataStore 확인을 위한 임시 코드
    fun clearPosts() {
        _postState.update {
            listOf()
        }
    }

    fun drawerIconClicked() {
        _event.tryEmit(MainActivityEvent.OpenDrawer)
    }

    fun appbarBackIconClicked() {
        _event.tryEmit(MainActivityEvent.NavigatePrev)
    }

    fun appbarCloseIconClicked() {
        _event.tryEmit(MainActivityEvent.NavigateClose)
    }

    fun previewButtonClicked(index: Int) {
        updateSelectedIndex(index = index)
        _event.tryEmit(MainActivityEvent.NavigatePreview(index))
    }

    fun onPreviewFragmentShowing() {
        _uiState.update {
            it.copy(
                isPreviewFragmentShowing = true
            )
        }
    }

    private fun updateSelectedIndex(index: Int){
        _uiState.update {
            it.copy(
                selectedIndex = index,
                focusedIndex = 0
            )
        }
    }

    fun onPreviewFragmentClosing() {
        _uiState.update {
            it.copy(
                isPreviewFragmentShowing = false,
                selectedIndex = -1,
                focusedIndex = -1
            )
        }
    }

    fun onBottomSheetChanged(isExpanded: Boolean) {
        _uiState.update {
            it.copy(isBottomSheetExpanded = isExpanded)
        }
    }

    fun onMarkerClicked(tag: SnapPointTag) {
        updateClickedSnapPoint(tag.postIndex, tag.snapPointIndex)
        _event.tryEmit(MainActivityEvent.NavigatePreview(tag.postIndex))
    }

    fun focusOfImageMoved(imageIndex: Int) {
        updateClickedSnapPoint(_uiState.value.selectedIndex, imageIndex)
    }

    private fun updateClickedSnapPoint(postIndex: Int, snapPointIndex: Int) {
        _uiState.update {
            it.copy(
                selectedIndex = postIndex,
                focusedIndex = snapPointIndex)
        }
    }

    fun updateAutoCompleteTexts(texts: List<String>) {
        _searchViewUiState.update {
            it.copy(texts = texts)
        }
    }

    private fun moveCameraToAddress(index: Int) {
        _event.tryEmit(MainActivityEvent.MoveCameraToAddress(index))
    }

    fun navigateSignIn() {
        _event.tryEmit(MainActivityEvent.NavigateSignIn)
    }
}