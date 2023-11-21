package com.boostcampwm2023.snappoint.presentation.preview

import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.boostcampwm2023.snappoint.databinding.ItemImagePreviewBinding

class PreviewViewHolder(private val binding: ItemImagePreviewBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(uri: Uri, description: String) {
        with(binding){
            ivImagePreview.load(uri) {
                scale(coil.size.Scale.FILL)
            }
            tvImageDescription.text = description
        }
    }
}