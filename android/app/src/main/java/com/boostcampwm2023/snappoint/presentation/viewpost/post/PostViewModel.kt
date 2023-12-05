package com.boostcampwm2023.snappoint.presentation.viewpost.post

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.data.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _event: MutableSharedFlow<PostEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<PostEvent> = _event.asSharedFlow()

    fun navigateToPrevious() {
        _event.tryEmit(PostEvent.NavigatePrev)
    }
}