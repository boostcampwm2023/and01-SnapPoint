package com.boostcampwm2023.snappoint.presentation.around

import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemAroundPostBinding

class PostItemViewHolder(
    private val binding: ItemAroundPostBinding,
    private val onExpandButtonClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PostState, index: Int) {
        with(binding) {
            postItem = item

            btnExpand.setOnClickListener {
                onExpandButtonClicked(index)
            }
        }
    }
}