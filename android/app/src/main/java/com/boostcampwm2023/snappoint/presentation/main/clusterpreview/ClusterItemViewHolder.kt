package com.boostcampwm2023.snappoint.presentation.main.clusterpreview

import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.boostcampwm2023.snappoint.databinding.ItemClusterImageBinding
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

class ClusterItemViewHolder(
    private val binding: ItemClusterImageBinding,
    private val onItemClicked: (Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PostBlockState, index: Int) {
        with(binding) {
            ivImage.load(item.content) {
                memoryCachePolicy(CachePolicy.ENABLED)
            }
            root.setOnClickListener {
                onItemClicked(index)
            }
        }
    }
}