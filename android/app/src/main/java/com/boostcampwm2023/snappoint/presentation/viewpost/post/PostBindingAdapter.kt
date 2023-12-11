package com.boostcampwm2023.snappoint.presentation.viewpost.post

import android.util.Log
import androidx.databinding.BindingAdapter
import com.boostcampwm2023.snappoint.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

@BindingAdapter("onNavigationClick")
fun MaterialToolbar.bindOnNavigationIconClicked(event: () -> Unit) {
    setNavigationOnClickListener { event() }
}

@BindingAdapter("onMenuItemClick")
fun MaterialToolbar.bindOnMenuItemClicked(event: (Any) -> Unit) {
    this.setOnMenuItemClickListener { menuItem->
        Log.d("LOG", "id: ${menuItem.itemId}")
        event.invoke(menuItem.itemId)
        true
    }
}

@BindingAdapter("mark")
fun FloatingActionButton.updateMark(isEnabled: Boolean) {
    if(isEnabled) {
        this.setImageResource(R.drawable.icon_popular_post)
    } else {
        this.setImageResource(R.drawable.icon_favorite_border)
    }
}