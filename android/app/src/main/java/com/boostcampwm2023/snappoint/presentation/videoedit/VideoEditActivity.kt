package com.boostcampwm2023.snappoint.presentation.videoedit

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.view.doOnLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.transformer.Composition
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import androidx.media3.transformer.Transformer.Listener
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityVideoEditBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@UnstableApi @AndroidEntryPoint
class VideoEditActivity : BaseActivity<ActivityVideoEditBinding>(R.layout.activity_video_edit) {

    private val viewModel: VideoEditViewModel by viewModels()

    private var postIndex = 0

    private lateinit var file: File
    private lateinit var trans: Transformer
    private lateinit var mediaItem: MediaItem



     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         getIntentExtra()

         initMediaItem(savedInstanceState)

         createExternalCacheFile()

         initBinding()

         collectViewModelData()

         initTransFormer()

    }

    private fun initMediaItem(savedInstanceState: Bundle?) {
        mediaItem = MediaItem.fromUri(viewModel.uri.value.toUri())

        val mediaMetadataRetriever = MediaMetadataRetriever().apply {
            setDataSource(this@VideoEditActivity, viewModel.uri.value.toUri())
        }
        val videoLengthInMs = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
        if(savedInstanceState == null) {
            viewModel.updateVideoLengthWithRightThumb(videoLengthInMs)
        }

    }

    private fun initTransFormer() {

        trans = Transformer.Builder(this@VideoEditActivity).setVideoMimeType(MimeTypes.VIDEO_H265).setAudioMimeType(MimeTypes.AUDIO_AAC).build()

        trans.addListener(object : Listener{
            override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                super.onCompleted(composition, exportResult)
                viewModel.finishLoading()
                intent.putExtra("path", file.path)
                setResult(RESULT_OK, intent)
                finish()

            }
            override fun onError(
                composition: Composition,
                exportResult: ExportResult,
                exportException: ExportException
            ) {
                super.onError(composition, exportResult, exportException)
                viewModel.finishLoading()
                showToastMessage(exportException.message?:"error")
            }
        })
    }

    private fun createExternalCacheFile() {

        file = File(externalCacheDir, "video_edited.mp4")

        try{
            if (file.exists() && !file.delete()) {
                throw IllegalStateException("Could not delete the previous export output file")
            }
            if (!file.createNewFile()) {
                throw IllegalStateException("Could not create the export output file")
            }
        } catch (e:Exception){
            Log.d("TAG", "createExternalCacheFile: ${e.message}")
        }

    }

    private fun collectViewModelData() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                launch {
                    viewModel.recentState.collect{
                        moveSeekPositionMs(position = it )
                    }
                }

            }
        }
    }

    private fun moveSeekPositionMs(position: Long) {
        binding.pv.player?.seekTo(position)
    }

    private fun initBinding() {
        with(binding){
            vm = viewModel
            pv.player = ExoPlayer.Builder(this@VideoEditActivity).build().also {
                it.setMediaItem(mediaItem)
                it.prepare()
            }

            pv.useController = false

            btnCancel.setOnClickListener {
                finish()
            }
            btnConfirm.setOnClickListener {
                viewModel.startLoading()
                trans.start(mediaItem, file.path)
            }

            tlv.doOnLayout {
                viewModel.updateTLVSize(it.width, it.height)
            }
        }
    }

    private fun getIntentExtra() {
        val uri = intent.getStringExtra("uri")?:""
        viewModel.setUri(uri)
        postIndex = intent.getIntExtra("index",0) ?: 0
    }

}