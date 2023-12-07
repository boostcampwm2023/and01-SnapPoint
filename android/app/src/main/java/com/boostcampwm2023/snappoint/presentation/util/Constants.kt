package com.boostcampwm2023.snappoint.presentation.util

import com.boostcampwm2023.snappoint.BuildConfig

object Constants {
    const val BOTTOM_SHEET_HALF_EXPANDED_RATIO: Float = 0.45f
    const val API_KEY = BuildConfig.MAPS_API_KEY
    const val EMAIL_DUPLICATE_ERROR: String = "HTTP 409 Conflict"
    const val UUID_BUNDLE_KEY: String = "uuid"
    const val IS_LOCAL_POST_BUNDLE_KEY: String = "isLocalPost"
    const val CLUSTER_TEXT_SIZE = 16
    const val MIN_CLUSTER_SIZE = 2
}