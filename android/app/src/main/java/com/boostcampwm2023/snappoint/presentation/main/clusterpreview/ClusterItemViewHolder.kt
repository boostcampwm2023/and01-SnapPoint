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
            when (item) {
                is PostBlockState.IMAGE -> {
                    ivImage.load(item.url480P) {
                        memoryCachePolicy(CachePolicy.ENABLED)
                    }
                }

                is PostBlockState.VIDEO -> {
                    ivImage.load(item.thumbnail480P) {
                        memoryCachePolicy(CachePolicy.ENABLED)
                    }
                }

                else -> {}
            }
            root.setOnClickListener {
                onItemClicked(index)
            }
        }
    }
}