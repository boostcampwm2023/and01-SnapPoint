package com.boostcampwm2023.snappoint.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SnapPointTag(
    val postUuid: String,
    val blockUuid: String
) : Parcelable
