package com.boostcampwm2023.snappoint.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SnapPointTag(
    val postIndex: Int,
    val snapPointIndex: Int
) : Parcelable
