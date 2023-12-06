package com.boostcampwm2023.snappoint.presentation.videoedit

import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityVideoEditBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoEditActivity : BaseActivity<ActivityVideoEditBinding>(R.layout.activity_video_edit) {

    private val viewModel: VideoEditViewModel by viewModels()

    private var index = 0
    private lateinit var uri: Uri

    @OptIn(UnstableApi::class) override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getIntentExtra()

        initBinding()


        val mediaItem = MediaItem.fromUri(uri)
        val mediaMetadataRetriever = MediaMetadataRetriever().apply {
            setDataSource(this@VideoEditActivity, uri)
        }
        val videoLengthInMs = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong() * 1000
        binding.pv.player = ExoPlayer.Builder(this).build().also {
            it.setMediaItem(mediaItem)
            it.prepare()
        }

        binding.pv.useController = false

        binding.btnCancel.setOnClickListener {
            binding.pv.player?.play()
        }
        binding.btnConfirm.setOnClickListener {
            binding.pv.player?.seekTo(0)
        }

    }

    private fun initBinding() {
        with(binding){
            vm = viewModel
        }
    }

    private fun getIntentExtra() {
        uri = intent.getStringExtra("uri")?.toUri() ?: return
        index = intent.getIntExtra("index",0) ?: 0
    }

}