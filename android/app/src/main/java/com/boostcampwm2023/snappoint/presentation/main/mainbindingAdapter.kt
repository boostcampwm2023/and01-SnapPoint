package com.boostcampwm2023.snappoint.presentation.main

import androidx.databinding.BindingAdapter
import com.google.android.material.search.SearchBar


@BindingAdapter("drawer_icon_click")
fun bindSearchBar(view: SearchBar, event: () -> Unit){
    view.setNavigationOnClickListener {
        event.invoke()
    }
}