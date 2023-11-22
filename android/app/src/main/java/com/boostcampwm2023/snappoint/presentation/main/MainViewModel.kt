package com.boostcampwm2023.snappoint.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.data.repository.PostRepository
import com.boostcampwm2023.snappoint.presentation.model.PositionState
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.model.SnapPointTag
import com.google.android.gms.maps.model.MarkerOptions
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
class MainViewModel @Inject constructor(
    private val postRepository: PostRepository
) :ViewModel(){


    private val _uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<MainActivityEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<MainActivityEvent> = _event.asSharedFlow()

    init {
        loadPosts()
    }

    private fun loadPosts() {
        _uiState.update {
            MainUiState(
                posts = listOf(
                    PostSummaryState(
                        title = "하이",
                        author = "원승빈",
                        timeStamp = "123",
                        postBlocks = listOf(
                            PostBlockState.STRING(
                                content = "안녕하세요, 하하"
                            ),
                            PostBlockState.IMAGE(
                                content = "https://health.chosun.com/site/data/img_dir/2023/07/17/2023071701753_0.jpg",
                                position = PositionState(10.0, 10.0),
                                description = "고양이입니다.",
                                address = "고양이를 발견한 동네"
                            ),PostBlockState.IMAGE(
                                content = "https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/201901/20/28017477-0365-4a43-b546-008b603da621.jpg",
                                position = PositionState(10.1, 10.1),
                                description = "강아징입니다.",
                                address = "내가 키우는 강아지"
                            ),
                            PostBlockState.STRING(
                                content = "ㅎㅇ염"
                            ),PostBlockState.STRING(
                                content = "동물원갔다왔슴다 ㅋ"
                            ),
                            PostBlockState.IMAGE(
                                content = "https://i.namu.wiki/i/Nvsy3_i1lyInOB79UBbcDeR6MocJ4C8TBN8NjepPwqTnojCbb3Xwge9gQXfAGgW74ZA3c3i16odhBLE0bSwgFA.webp",
                                position = PositionState(10.4, 10.3),
                                description = "이것은 악어~",
                                address = "제일 좋아하는 동물이에용"
                            ),
                            PostBlockState.IMAGE(
                                content = "https://i.namu.wiki/i/Nvsy3_i1lyInOB79UBbcDeR6MocJ4C8TBN8NjepPwqTnojCbb3Xwge9gQXfAGgW74ZA3c3i16odhBLE0bSwgFA.webp",
                                position = PositionState(10.8, 9.8),
                                description = "이것은 악어~",
                                address = "제일 좋아하는 동물이에용"
                            ),
                        )
                    ),
                    PostSummaryState(
                        title = "마커 테스트",
                        author = "TEST",
                        timeStamp = "1",
                        postBlocks = listOf(
                            PostBlockState.STRING(
                                content = "123"
                            ),
                            PostBlockState.IMAGE(
                                content = "https://i.namu.wiki/i/Nvsy3_i1lyInOB79UBbcDeR6MocJ4C8TBN8NjepPwqTnojCbb3Xwge9gQXfAGgW74ZA3c3i16odhBLE0bSwgFA.webp",
                                position = PositionState(10.0, 9.7),
                                description = "test",
                                address = "address"
                            ),
                            PostBlockState.IMAGE(
                                content = "https://i.namu.wiki/i/Nvsy3_i1lyInOB79UBbcDeR6MocJ4C8TBN8NjepPwqTnojCbb3Xwge9gQXfAGgW74ZA3c3i16odhBLE0bSwgFA.webp",
                                position = PositionState(9.9, 10.1),
                                description = "test",
                                address = "address"
                            ),
                            PostBlockState.IMAGE(
                                content = "https://i.namu.wiki/i/Nvsy3_i1lyInOB79UBbcDeR6MocJ4C8TBN8NjepPwqTnojCbb3Xwge9gQXfAGgW74ZA3c3i16odhBLE0bSwgFA.webp",
                                position = PositionState(10.3, 10.5),
                                description = "test",
                                address = "address"
                            ),
                        )
                    ),
                )
            )
        }
        createMarkers()
    }

    private fun createMarkers() {
        _uiState.update {
            it.copy(
                snapPoints =
                _uiState.value.posts.mapIndexed { index, postSummaryState ->
                    SnapPointState(
                        index = index,
                        markerOptions = postSummaryState.postBlocks.filterIsInstance<PostBlockState.IMAGE>().map {
                            MarkerOptions().apply {
                                position(it.position.asLatLng())
                            }
                        }
                    )
                }
            )
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
            it.copy(selectedIndex = index)
        }
    }


    fun onPreviewFragmentClosing() {
        _uiState.update {
            it.copy(
                isPreviewFragmentShowing = false,
                selectedIndex = -1
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
    }

    private fun updateClickedSnapPoint(postIndex: Int, snapPointIndex: Int) {
        _uiState.update {
            it.copy(
                selectedIndex = postIndex,
                focusedIndex = snapPointIndex)
        }
    }

}