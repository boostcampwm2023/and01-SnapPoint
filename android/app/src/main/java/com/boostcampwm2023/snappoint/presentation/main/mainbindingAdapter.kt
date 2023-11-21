package com.boostcampwm2023.snappoint.presentation.main

import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.search.SearchBar


@BindingAdapter("drawer_icon_click")
fun bindSearchBar(view: SearchBar, event: () -> Unit){
    view.setNavigationOnClickListener {
        event.invoke()
    }
}

@BindingAdapter("draggable")
fun LinearLayout.setDraggable(value: Boolean) {
    val behavior = BottomSheetBehavior.from(this)
    behavior.isDraggable = value
}