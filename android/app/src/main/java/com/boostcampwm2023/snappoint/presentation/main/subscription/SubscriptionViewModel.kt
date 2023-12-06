package com.boostcampwm2023.snappoint.presentation.main.subscription

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.data.repository.RoomRepository
import com.boostcampwm2023.snappoint.presentation.util.SignInUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val signInUtil: SignInUtil
) : ViewModel() {

    fun getSavedPost() {
        roomRepository.getLocalPosts(signInUtil.getEmail())
            .onEach {
                Log.d("LOG", it.toString())
            }.catch {
                Log.d("LOG", "Catch: ${it.message}}")
            }.launchIn(viewModelScope)
    }
}