package com.boostcampwm2023.snappoint.presentation.viewpost

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.main.MainUiState
import com.boostcampwm2023.snappoint.presentation.model.PositionState
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ViewPostViewModel @Inject constructor() : ViewModel() {

    private val _selectedIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    fun updateSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

}