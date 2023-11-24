package com.boostcampwm2023.snappoint.presentation.main

import android.annotation.SuppressLint
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.boostcampwm2023.snappoint.R
import com.google.android.material.appbar.MaterialToolbar
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

@BindingAdapter("navigation_icon_click")
fun navigationIconClick(view: MaterialToolbar, event: () -> Unit) {
    view.setNavigationOnClickListener {
        event.invoke()
    }
}

@BindingAdapter("menu_item_click")
fun menuItemClick(view: MaterialToolbar, event: () -> Unit) {
    view.setOnMenuItemClickListener {
        return@setOnMenuItemClickListener when (it.itemId) {
            R.id.preview_close -> {
                event.invoke()
                true
            }

            else -> {
                true
            }
        }
    }
}