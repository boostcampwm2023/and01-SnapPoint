package com.boostcampwm2023.snappoint.presentation.around

import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemAroundPostBinding
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState

class PostItemViewHolder(private val binding: ItemAroundPostBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PostSummaryState) {
        binding.tvPostTitle.text = item.title
        binding.tvPostTimestamp.text = item.timeStamp
    }
}