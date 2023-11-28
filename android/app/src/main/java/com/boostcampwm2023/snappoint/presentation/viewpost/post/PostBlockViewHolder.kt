package com.boostcampwm2023.snappoint.presentation.viewpost.post

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.boostcampwm2023.snappoint.databinding.ItemImagePostBinding
import com.boostcampwm2023.snappoint.databinding.ItemTextPostBinding
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

class TextBlockViewHolder(private val binding: ItemTextPostBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PostBlockState.TEXT) {
        binding.block = item
    }
}

class ImageBlockViewHolder(private val binding: ItemImagePostBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PostBlockState.IMAGE) {
        binding.block = item
    }
}

@BindingAdapter("image")
fun ImageView.bindImage(url: String) {
    load(url) {
        memoryCachePolicy(CachePolicy.ENABLED)
    }
}