package com.boostcampwm2023.snappoint.presentation.viewpost.post

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.datastore.preferences.protobuf.ExperimentalApi
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.boostcampwm2023.snappoint.databinding.ItemImagePostBinding
import com.boostcampwm2023.snappoint.databinding.ItemTextPostBinding
import com.boostcampwm2023.snappoint.databinding.ItemVideoPostBinding
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

@UnstableApi
class VideoBlockViewHolder(private val binding: ItemVideoPostBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PostBlockState.VIDEO) {
        binding.block = item

        val mediaItem = MediaItem.fromUri(item.content)

        binding.pv.player = ExoPlayer.Builder(itemView.context).build().also {
            it.setMediaItem(mediaItem)
            it.prepare()

        }
    }
}
@BindingAdapter("image")
fun ImageView.bindImage(url: String) {
    load(url) {
        memoryCachePolicy(CachePolicy.ENABLED)
    }
}