package com.boostcampwm2023.snappoint.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor()
    :ViewModel(){


        fun sideSheetIconClicked() {
            Log.d("TAG", "sideSheetIconClicked: clicked!")
        }
}