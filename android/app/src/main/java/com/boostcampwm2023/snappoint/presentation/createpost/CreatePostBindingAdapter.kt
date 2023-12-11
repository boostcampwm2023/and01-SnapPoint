package com.boostcampwm2023.snappoint.presentation.createpost

import androidx.databinding.BindingAdapter
import com.boostcampwm2023.snappoint.R
import com.google.android.material.appbar.MaterialToolbar

@BindingAdapter("toolbar_title")
fun MaterialToolbar.bindToolbarTitle(uuid: String) {
    if(uuid.isBlank()) {
        this.setTitle(R.string.create_post_fragment_appbar_title)
    }else {
        this.setTitle(R.string.create_post_fragment_appbar_title_modify)
    }
}