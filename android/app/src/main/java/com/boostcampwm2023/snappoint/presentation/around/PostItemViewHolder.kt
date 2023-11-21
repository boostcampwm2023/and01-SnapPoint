package com.boostcampwm2023.snappoint.presentation.around

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemAroundPostBinding
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.util.ExpandButtonToggleAnimation

class PostItemViewHolder(
    private val binding: ItemAroundPostBinding,
    private val onExpandButtonClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PostSummaryState, index: Int, isExpanded: Boolean) {

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

    private fun toggleLayout(isExpanded: Boolean, view: View, tvBody: TextView, btnPreviewPost: Button, btnViewPost: Button) {
        ExpandButtonToggleAnimation.toggleArrow(view, isExpanded)
        if (isExpanded) {
            ExpandButtonToggleAnimation.expand(tvBody)
            ExpandButtonToggleAnimation.expand(btnPreviewPost)
            ExpandButtonToggleAnimation.expand(btnViewPost)
        } else {
            ExpandButtonToggleAnimation.collapse(tvBody)
            ExpandButtonToggleAnimation.collapse(btnPreviewPost)
            ExpandButtonToggleAnimation.collapse(btnViewPost)
        }
    }
}