package com.boostcampwm2023.snappoint.presentation.login

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter

@BindingAdapter("afterTextChanged")
fun EditText.afterTextChanged(event: (String) -> Unit) {
    this.addTextChangedListener {
        event(it.toString())
    }
}