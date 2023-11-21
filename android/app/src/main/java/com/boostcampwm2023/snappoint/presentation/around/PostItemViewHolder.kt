package com.boostcampwm2023.snappoint.presentation.around

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemAroundPostBinding
import com.boostcampwm2023.snappoint.presentation.util.ExpandButtonToggleAnimation

class PostItemViewHolder(
    private val binding: ItemAroundPostBinding,
    private val onExpandButtonClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PostState, index: Int, isExpanded: Boolean) {
        var expandState = isExpanded

        with(binding) {
            postItem = item
            isExpand = expandState

            btnExpand.setOnClickListener {
                expandState = expandState.not()
                toggleLayout(expandState, it, tvPostBody, btnPreviewPost, btnViewPost)
                onExpandButtonClicked(index)
            }
        }
    }

    private fun toggleLayout(isExpanded: Boolean, view: View, textView: TextView, button1: Button, button2: Button) {
        ExpandButtonToggleAnimation.toggleArrow(view, isExpanded)
        if (isExpanded) {
            ExpandButtonToggleAnimation.expand(textView)
            ExpandButtonToggleAnimation.expand(button1)
            ExpandButtonToggleAnimation.expand(button2)
        } else {
            ExpandButtonToggleAnimation.collapse(textView)
            ExpandButtonToggleAnimation.collapse(button1)
            ExpandButtonToggleAnimation.collapse(button2)
        }
    }
}