package com.boostcampwm2023.snappoint.presentation.around

import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemAroundPostBinding

class PostItemViewHolder(private val binding: ItemAroundPostBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PostState) {
        binding.tvPostTitle.text = item.title
        binding.tvPostTimestamp.text = item.timeStamp
        binding.tvPostBody.text = item.body
    }
}