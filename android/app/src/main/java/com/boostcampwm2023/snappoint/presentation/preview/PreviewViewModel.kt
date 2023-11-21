package com.boostcampwm2023.snappoint.presentation.preview

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.createpost.PositionState
import com.boostcampwm2023.snappoint.presentation.createpost.PostBlockState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<PreviewUiState> = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    init {
        val title: String = "TITLE"
        val timeStamp: String = "0 Days Ago"
        val list: List<PostBlockState> = listOf(
            PostBlockState.IMAGE(
                "세로로 길쭉해요!",
                Uri.parse("https://gist.github.com/rbybound/0e96e32b94fb721fe9f0c563f4ff0ee7/raw/27325596c284c204fd2f539ee44a4da93a929aa7/3-2-1-height.jpg"),
                PositionState(37.3586926, 127.1051209)
            ),
            PostBlockState.IMAGE(
                "쭈우우우우우우우욱~!",
                Uri.parse("https://gist.github.com/rbybound/0e96e32b94fb721fe9f0c563f4ff0ee7/raw/27325596c284c204fd2f539ee44a4da93a929aa7/3-2-1-width.jpg"),
                PositionState(37.3586926, 127.1051209)
            ),
            PostBlockState.IMAGE(
                "aaas dfad fa df aaasd fad fas df aaa sdfa dfasdf aaasd fadf asdf ",
                Uri.parse("https://gist.github.com/rbybound/0e96e32b94fb721fe9f0c563f4ff0ee7/raw/9410800afbdbe648274523b68e8311308a46c37a/3-1-0.jpg"),
                PositionState(37.3586926, 127.1051209)
            ),
            PostBlockState.IMAGE(
                "aaas dfad fa df aaasd fad fas df aaa sdfa dfasdf aaasd fadf asdf ",
                Uri.parse("https://gist.github.com/rbybound/0e96e32b94fb721fe9f0c563f4ff0ee7/raw/584774cf68f54fab1ef7e34fdfc9410c04521900/3-1-1.jpg"),
                PositionState(37.3586926, 127.1051209)
            ),
            PostBlockState.IMAGE(
                "][po ][ po[ p ][p o][ po[ p ][ po] [po [ p ][ po ][p o[ p ",
                Uri.parse("https://gist.github.com/rbybound/0e96e32b94fb721fe9f0c563f4ff0ee7/raw/584774cf68f54fab1ef7e34fdfc9410c04521900/3-1-2.jpg"),
                PositionState(37.3586926, 127.1051209)
            )
        )
        _uiState.update {
            it.copy(
                title = title,
                timeStamp = timeStamp,
                blocks = list
            )
        }
    }
}