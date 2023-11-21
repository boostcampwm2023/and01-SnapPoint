package com.boostcampwm2023.snappoint.presentation.main

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.data.repository.PostRepository
import com.boostcampwm2023.snappoint.presentation.model.PositionState
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.LatLng
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




    fun drawerIconClicked() {
        _event.tryEmit(MainActivityEvent.OpenDrawer)
    }
    init{
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
                            )
                        )
                    ),
                    PostSummaryState(
                        title = "놀러갔다온썰푼다",
                        author = "이정건",
                        timeStamp = "456",
                        postBlocks = listOf(
                            PostBlockState.IMAGE(
                                content = "https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/201901/20/28017477-0365-4a43-b546-008b603da621.jpg",
                                position = PositionState(10.1, 10.1),
                                description = "강아징입니다.",
                                address = "내가 키우는 강아지"
                            ),
                            PostBlockState.STRING(
                                content = "ㅎㅇ염"
                            ),
                        )
                    ),PostSummaryState(
                        title = "여기좋아용",
                        author = "안언수",
                        timeStamp = "678",
                        postBlocks = listOf(
                            PostBlockState.STRING(
                                content = "동물원갔다왔슴다 ㅋ"
                            ),
                            PostBlockState.IMAGE(
                                content = "https://i.namu.wiki/i/Nvsy3_i1lyInOB79UBbcDeR6MocJ4C8TBN8NjepPwqTnojCbb3Xwge9gQXfAGgW74ZA3c3i16odhBLE0bSwgFA.webp",
                                position = PositionState(10.4, 10.3),
                                description = "이것은 악어~",
                                address = "제일 좋아하는 동물이에용"
                            )
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
}