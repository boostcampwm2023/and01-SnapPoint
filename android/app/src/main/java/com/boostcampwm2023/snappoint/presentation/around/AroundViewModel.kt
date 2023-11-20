package com.boostcampwm2023.snappoint.presentation.around

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AroundViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<AroundUiState> = MutableStateFlow(AroundUiState())
    val uiState: StateFlow<AroundUiState> = _uiState.asStateFlow()
}