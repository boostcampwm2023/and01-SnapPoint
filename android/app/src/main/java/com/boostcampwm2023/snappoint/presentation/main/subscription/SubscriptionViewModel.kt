package com.boostcampwm2023.snappoint.presentation.main.subscription

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.data.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    fun getSavedPost() {
        roomRepository.getLocalPosts()
            .onEach {
                Log.d("LOG", it.toString())
            }.catch {
                Log.d("LOG", "Catch: ${it.message}}")
            }.launchIn(viewModelScope)
    }
}