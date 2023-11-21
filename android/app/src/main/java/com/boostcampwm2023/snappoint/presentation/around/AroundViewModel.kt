package com.boostcampwm2023.snappoint.presentation.around

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AroundViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<AroundUiState> =
        MutableStateFlow(AroundUiState())
    val uiState: StateFlow<AroundUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                posts = listOf(
                    PostState(
                        title = "노잼도시 청주를 여행해보자",
                        author = "양희범",
                        timeStamp = "1 Days Ago",
                        body = "동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하세"
                    ),
                    PostState(
                        title = "청주의 정통 맛집을 찾아서",
                        author = "주재현",
                        timeStamp = "2 Weeks Ago",
                        body = ""
                    ),
                    PostState(
                        title = "청주 야경 맛집 7선",
                        author = "이정건",
                        timeStamp = "3 Months Ago",
                        body = ""
                    ),
                    PostState(
                        title = "안녕하세요",
                        author = "원승빈",
                        timeStamp = "4 Years Ago",
                        body = ""
                    ),
                    PostState(
                        title = "노잼도시 청주를 여행해보자",
                        author = "양희범",
                        timeStamp = "1 Days Ago",
                        body = "동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하세"
                    ),
                    PostState(
                        title = "청주의 정통 맛집을 찾아서",
                        author = "주재현",
                        timeStamp = "2 Weeks Ago",
                        body = ""
                    ),
                    PostState(
                        title = "청주 야경 맛집 7선",
                        author = "이정건",
                        timeStamp = "3 Months Ago",
                        body = ""
                    ),
                    PostState(
                        title = "안녕하세요",
                        author = "원승빈",
                        timeStamp = "4 Years Ago",
                        body = ""
                    ),
                    PostState(
                        title = "노잼도시 청주를 여행해보자",
                        author = "양희범",
                        timeStamp = "1 Days Ago",
                        body = "동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하세"
                    ),
                    PostState(
                        title = "청주의 정통 맛집을 찾아서",
                        author = "주재현",
                        timeStamp = "2 Weeks Ago",
                        body = ""
                    ),
                    PostState(
                        title = "청주 야경 맛집 7선",
                        author = "이정건",
                        timeStamp = "3 Months Ago",
                        body = ""
                    ),
                    PostState(
                        title = "안녕하세요",
                        author = "원승빈",
                        timeStamp = "4 Years Ago",
                        body = ""
                    ),
                )
            )
        }
    }
}