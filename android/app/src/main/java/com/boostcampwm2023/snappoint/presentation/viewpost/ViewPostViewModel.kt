package com.boostcampwm2023.snappoint.presentation.viewpost

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.model.PositionState
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
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
class ViewPostViewModel @Inject constructor() : ViewModel() {

    private val _selectedIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    private val _posts: MutableStateFlow<List<PostSummaryState>> = MutableStateFlow(emptyList())
    val posts: StateFlow<List<PostSummaryState>> = _posts.asStateFlow()

    private val _event: MutableSharedFlow<ViewPostEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<ViewPostEvent> = _event.asSharedFlow()

    fun updateSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

    init {
        loadPosts()
    }

    private fun loadPosts() {
        _posts.update {
            listOf(
                PostSummaryState(
                    title = "하이",
                    author = "원승빈",
                    timeStamp = "2 Days Ago",
                    postBlocks = listOf(
                        PostBlockState.TEXT(
                            content = "안녕하세요, 하하"
                        ),
                        PostBlockState.IMAGE(
                            content = "https://health.chosun.com/site/data/img_dir/2023/07/17/2023071701753_0.jpg",
                            position = PositionState(37.421793077676774, -122.09180117366115),
                            description = "고양이입니다.",
                            address = "null"
                        ),
                        PostBlockState.IMAGE(
                            content = "https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/201901/20/28017477-0365-4a43-b546-008b603da621.jpg",
                            position = PositionState(37.41887606344049, -122.0879954078449),
                            description = "강아징입니다.",
                            address = "null"
                        ),
                        PostBlockState.TEXT(
                            content = "ㅎㅇ염"
                        ),
                        PostBlockState.TEXT(
                            content = "동물원갔다왔슴다 ㅋ"
                        ),
                        PostBlockState.IMAGE(
                            content = "https://i.namu.wiki/i/Nvsy3_i1lyInOB79UBbcDeR6MocJ4C8TBN8NjepPwqTnojCbb3Xwge9gQXfAGgW74ZA3c3i16odhBLE0bSwgFA.webp",
                            position = PositionState(37.42155682099068, -122.08342886715077),
                            description = "이것은 악어~",
                            address = "null"
                        ),
                        PostBlockState.IMAGE(
                            content = "https://upload.wikimedia.org/wikipedia/commons/4/41/Siberischer_tiger_de_edit02.jpg",
                            position = PositionState(37.4227919844394, -122.08028507548029),
                            description = "어흥",
                            address = "null"
                        ),
                    )
                ),
                PostSummaryState(
                    title = "여름 철새 구경",
                    author = "익명",
                    timeStamp = "3 Weeks Ago",
                    postBlocks = listOf(
                        PostBlockState.TEXT(
                            content = "123"
                        ),
                        PostBlockState.IMAGE(
                            content = "https://upload.wikimedia.org/wikipedia/commons/8/85/Columbina_passerina.jpg",
                            position = PositionState(37.40837052881207, -122.10293026989889),
                            description = "비둘기야 먹자 구구구구",
                            address = "address"
                        ),
                        PostBlockState.IMAGE(
                            content = "https://upload.wikimedia.org/wikipedia/commons/f/f0/Nipponia_nippon_20091230131054.png",
                            position = PositionState(37.40272239693208, -122.06577824456974),
                            description = "떴따 오기",
                            address = "address"
                        ),
                        PostBlockState.IMAGE(
                            content = "https://upload.wikimedia.org/wikipedia/commons/8/82/Watercock_%28Gallicrex_cinerea%29.jpg",
                            position = PositionState(37.414911773823924, -122.0536102126485),
                            description = "뜸 부기",
                            address = "address"
                        ),
                    )
                ),
            )
        }
    }

    fun finishPostView() {
        _event.tryEmit(ViewPostEvent.FinishActivity)
    }
}