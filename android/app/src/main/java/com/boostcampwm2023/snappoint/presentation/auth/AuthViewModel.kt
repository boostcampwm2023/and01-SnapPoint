package com.boostcampwm2023.snappoint.presentation.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<AuthUiState> = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateBottomSheetHeight(height: Int) {
        _uiState.update {
            it.copy(bottomSheetHeight = height)
        }
    }

    fun updateFragmentHeight(fragmentHeight: Int, handleHeight: Int) {
        _uiState.update {
            it.copy(fragmentHeight = fragmentHeight, handleHeight = handleHeight)
        }
    }

    fun activateBottomSheet() {
        if (uiState.value.isBottomSheetActivated.not()) {
            _uiState.update {
                it.copy(isBottomSheetActivated = true)
            }
        }
    }
}