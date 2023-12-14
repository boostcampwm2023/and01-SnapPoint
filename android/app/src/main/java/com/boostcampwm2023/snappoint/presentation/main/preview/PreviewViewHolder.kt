package com.boostcampwm2023.snappoint.presentation.main.preview

import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.boostcampwm2023.snappoint.databinding.ItemImagePreviewBinding

class PreviewViewHolder(
    private val binding: ItemImagePreviewBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(url: String, description: String) {
        with(binding){
            ivPreviewCarouselImage.load(url) {
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
                scale(coil.size.Scale.FILL)
            }
            tvPreviewImageDescription.text = description
        }
    }
}