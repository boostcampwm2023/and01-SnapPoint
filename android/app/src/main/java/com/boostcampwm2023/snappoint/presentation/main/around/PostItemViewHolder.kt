package com.boostcampwm2023.snappoint.presentation.main.around

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.boostcampwm2023.snappoint.databinding.ItemAroundPostBinding
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.util.ExpandButtonToggleAnimation

class PostItemViewHolder(
    private val binding: ItemAroundPostBinding,
    private val onExpandButtonClicked: (Int) -> Unit,
    private val onPreviewButtonClicked: (Int) -> Unit,
    private val onViewPostButtonClicked: (Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(item: PostSummaryState, index: Int, isExpanded: Boolean) {

        var expandState = isExpanded

        with(binding) {
            postItem = item
            isExpand = expandState

            root.setOnClickListener {
                expandState = expandState.not()
                toggleLayout(expandState, btnExpand, layoutExpanded)
                onExpandButtonClicked(index)
            }
            btnExpand.setOnClickListener {
                expandState = expandState.not()
                toggleLayout(expandState, it, layoutExpanded)
                onExpandButtonClicked(index)
            }
            btnPreviewPost.setOnClickListener {
                onPreviewButtonClicked(index)
            }
            btnViewPost.setOnClickListener {
                onViewPostButtonClicked(index)
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

@BindingAdapter("profileImage")
fun ImageView.bindProfileImage(postBlocks: List<PostBlockState>) {
    postBlocks.forEach { block ->
        when (block) {
            is PostBlockState.IMAGE -> {
                load(block.content) {
                    memoryCachePolicy(CachePolicy.ENABLED)
                    transformations(CircleCropTransformation())
                }
                return
            }

            else -> {}
        }
    }
}