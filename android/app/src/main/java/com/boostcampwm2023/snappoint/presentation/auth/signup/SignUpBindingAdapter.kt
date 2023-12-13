package com.boostcampwm2023.snappoint.presentation.auth.signup

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("setErrorMessage")
fun TextInputLayout.setErrorMessage(id: Int?) {
    this.error = id?.let {
        this.isErrorEnabled = true
        context.getString(it)
    } ?: run {
        this.isErrorEnabled = false
        null
    }
}
