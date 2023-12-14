package com.boostcampwm2023.snappoint.presentation.util

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("timeStamp")
fun TextView.bindTimeStamp(createdAt: String) {
    if (createdAt == "") return
    text = TimeStampUtil.getTimeStamp(TimeStampUtil.stringToMills(createdAt), resources)
}