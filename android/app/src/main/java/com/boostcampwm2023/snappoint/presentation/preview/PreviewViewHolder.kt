package com.boostcampwm2023.snappoint.presentation.preview

import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.boostcampwm2023.snappoint.databinding.ItemImagePreviewBinding

class PreviewViewHolder(
    private val binding: ItemImagePreviewBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(uri: Uri, description: String) {
        with(binding){
            ivPreviewCarouselImage.load(uri) {
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
                scale(coil.size.Scale.FILL)
            }
            tvPreviewImageDescription.text = description
        }
    }
}