package com.boostcampwm2023.snappoint.presentation.viewpost.post

import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.MaterialToolbar

@BindingAdapter("onNavigationClick")
fun MaterialToolbar.bindOnNavigationIconClicked(event: () -> Unit) {
    setNavigationOnClickListener { event() }
}