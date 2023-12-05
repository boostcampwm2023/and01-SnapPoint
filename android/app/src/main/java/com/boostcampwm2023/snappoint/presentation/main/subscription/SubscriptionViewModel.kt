package com.boostcampwm2023.snappoint.presentation.main.subscription

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.data.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {


}