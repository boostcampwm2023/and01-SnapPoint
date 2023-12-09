package com.boostcampwm2023.snappoint.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.data.repository.PostRepository
import com.boostcampwm2023.snappoint.data.repository.RoomRepository
import com.boostcampwm2023.snappoint.presentation.main.search.SearchViewUiState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.model.SnapPointTag
import com.boostcampwm2023.snappoint.presentation.util.UserInfo
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
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _postState: MutableStateFlow<List<PostSummaryState>> = MutableStateFlow(emptyList())
    val postState: StateFlow<List<PostSummaryState>> = _postState.asStateFlow()

    private val _uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _markerState: MutableStateFlow<MarkerUiState> = MutableStateFlow(MarkerUiState())
    val markerState: StateFlow<MarkerUiState> = _markerState.asStateFlow()

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
            .onStart { startLoading() }
            .catch {
                Log.d("TAG", "loadPosts: $it")
                _event.tryEmit(MainActivityEvent.GetAroundPostFailed)

            }
            .onCompletion { finishLoading() }
            .onEach { response ->
                _postState.value = response
                Log.d("TAG", "loadPosts: $response")
                _event.tryEmit(MainActivityEvent.HalfOpenBottomSheet)
            }.launchIn(viewModelScope)
    }

    fun loadLocalPost() {
        roomRepository.getAllLocalPost(UserInfo.getEmail())
            .onStart {
                startLoading()
            }
            .catch {
                _event.tryEmit(MainActivityEvent.GetAroundPostFailed)
            }
            .onEach { posts ->
                _postState.update { posts }
                _event.tryEmit(MainActivityEvent.HalfOpenBottomSheet)
            }
            .takeWhile {
                false
            }
            .launchIn(viewModelScope)
        finishLoading()
    }

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

    fun onClusterPreviewShowing() {
        _uiState.update {
            it.copy(isClusterPreviewShowing = true)
        }
    }

    fun onClusterPreviewClosing() {
        _uiState.update {
            it.copy(isClusterPreviewShowing = false)
        }
    }

    private fun updateSelectedIndex(index: Int) {
        _markerState.value = MarkerUiState(
            selectedIndex = index,
            focusedIndex = 0
        )
    }

    fun onPreviewFragmentClosing() {
        _markerState.value = MarkerUiState(
            selectedIndex = -1,
            focusedIndex = -1
        )
        _uiState.update {
            it.copy(
                isPreviewFragmentShowing = false
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

    fun onClusterClicked(cluster: List<SnapPointTag>) {
        _event.tryEmit(MainActivityEvent.NavigateCluster(cluster))
    }

    fun focusOfImageMoved(imageIndex: Int) {
        updateClickedSnapPoint(_markerState.value.selectedIndex, imageIndex)
    }

    fun startLoading() {
        _uiState.update { it.copy(isLoading = true) }
    }

    fun finishLoading() {
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun updateClickedSnapPoint(postIndex: Int, snapPointIndex: Int) {
        _markerState.value = MarkerUiState(
            selectedIndex = postIndex,
            focusedIndex = snapPointIndex
        )
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

    fun onMapReady() {
        _event.tryEmit(MainActivityEvent.CheckPermissionAndMoveCameraToUserLocation)
    }
}