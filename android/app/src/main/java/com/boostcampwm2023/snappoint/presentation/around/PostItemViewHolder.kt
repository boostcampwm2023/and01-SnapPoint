package com.boostcampwm2023.snappoint.presentation.around

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemAroundPostBinding
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.util.ExpandButtonToggleAnimation

class PostItemViewHolder(
    private val binding: ItemAroundPostBinding,
    private val onExpandButtonClicked: (Int) -> Unit,
    private val onPreviewButtonClicked: (Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(item: PostSummaryState, index: Int, isExpanded: Boolean) {

        var expandState = isExpanded

        with(binding) {
            postItem = item
            isExpand = expandState

            btnExpand.setOnClickListener {
                expandState = expandState.not()
                toggleLayout(expandState, it, layoutExpanded)
                onExpandButtonClicked(index)
            }

            btnPreviewPost.setOnClickListener {
                onPreviewButtonClicked(index)
            }
        }
    }

    private fun toggleLayout(isExpanded: Boolean, view: View, expandedLayout: LinearLayout) {
        ExpandButtonToggleAnimation.toggleArrow(view, isExpanded)
        if (isExpanded) {
            ExpandButtonToggleAnimation.expand(expandedLayout)
        } else {
            ExpandButtonToggleAnimation.collapse(expandedLayout)
        }
    }
}