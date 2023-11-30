package com.boostcampwm2023.snappoint.presentation.util

import android.icu.text.DecimalFormat

fun Double.untilSixAfterDecimalPoint(): Double {
    val decimalFormat = DecimalFormat("#.000000")
    return decimalFormat.format(this).toDouble()
}