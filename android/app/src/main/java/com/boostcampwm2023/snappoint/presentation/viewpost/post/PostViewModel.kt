package com.boostcampwm2023.snappoint.presentation.viewpost.post

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.data.repository.RoomRepository
import com.boostcampwm2023.snappoint.data.repository.UserInfoRepository
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val userInfoRepository: UserInfoRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<PostUiState> = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<PostEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<PostEvent> = _event.asSharedFlow()

    fun initMenu(postOwnerEmail: String) {
        if(postOwnerEmail == userInfoRepository.getEmail()){
            _uiState.update {
                it.copy(isReadOnly = false)
            }
        }
    }

    fun navigateToPrevious() {
        _event.tryEmit(PostEvent.NavigatePrev)
    }

    fun onMenuItemClick(menuItemId: Any) {
        _event.tryEmit(PostEvent.MenuItemClicked(menuItemId.toString().toInt()))
    }

    fun onLikeButtonClick() {
        if(uiState.value.isLikeEnabled) {
            _event.tryEmit(PostEvent.DeletePost)
            _uiState.update { it.copy(isLikeEnabled = false) }
        } else {
            _event.tryEmit(PostEvent.SavePost)
            _uiState.update { it.copy(isLikeEnabled = true) }
        }
    }

    fun updateLikeMarkState(uuid: String) {
        roomRepository.getPost(uuid, userInfoRepository.getEmail())
            .flowOn(Dispatchers.IO)
            .onEach { post ->
                _uiState.update {
                    it.copy(
                        isLikeEnabled = post.isNotEmpty()
                    )
                }
            }
            .catch {
                Log.d("LOG", "Catch: ${it.message}")
            }
            .takeWhile {
                false
            }.launchIn(viewModelScope)
    }

    fun saveCurrentPostToLocal(post: PostSummaryState) {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.insertPosts(post, userInfoRepository.getEmail())
        }
    }

    fun deleteCurrentPostFromLocal(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.deletePost(uuid, userInfoRepository.getEmail())
        }
    }
}