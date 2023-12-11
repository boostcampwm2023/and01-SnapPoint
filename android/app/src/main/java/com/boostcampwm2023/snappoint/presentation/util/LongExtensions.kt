package com.boostcampwm2023.snappoint.presentation.util

import java.text.SimpleDateFormat
import java.util.Date


fun Long.toDisplayTime():String{
    val format = SimpleDateFormat("mm:ss")
    val date = Date(this)
    return format.format(date)
}