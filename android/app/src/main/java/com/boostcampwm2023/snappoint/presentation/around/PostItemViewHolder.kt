package com.boostcampwm2023.snappoint.presentation.around

import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemAroundPostBinding

class PostItemViewHolder(private val binding: ItemAroundPostBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PostState) {
        with(binding) {
            postItem = item

            btnExpand.setOnClickListener {

            }
        }
    }
}