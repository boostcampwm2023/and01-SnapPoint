package com.boostcampwm2023.snappoint.presentation.util

import android.content.res.Resources

fun Int.px(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}


fun Int.pxFloat(): Float {
    return this * Resources.getSystem().displayMetrics.density
}