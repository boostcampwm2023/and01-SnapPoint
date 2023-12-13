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
import androidx.media3.common.MediaItem.ClippingConfiguration
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
import com.boostcampwm2023.snappoint.presentation.util.CacheManager.createExternalCacheFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@UnstableApi @AndroidEntryPoint
class VideoEditActivity : BaseActivity<ActivityVideoEditBinding>(R.layout.activity_video_edit) {

    private val viewModel: VideoEditViewModel by viewModels()

    private lateinit var file: File
    private lateinit var trans: Transformer
    private lateinit var mediaItem: MediaItem



     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         getIntentExtra()

         initMediaItem(savedInstanceState)


         initBinding()

         collectViewModelData()

         initTransFormer()

    }

    private fun initMediaItem(savedInstanceState: Bundle?) {
        mediaItem = MediaItem.fromUri(viewModel.uri.value.toUri())
        file = createExternalCacheFile(this)

        val mediaMetadataRetriever = MediaMetadataRetriever().apply {
            setDataSource(this@VideoEditActivity, viewModel.uri.value.toUri())
        }
        val videoLengthInMs = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
        if(savedInstanceState == null) {
            viewModel.updateVideoLengthWithRightThumb(videoLengthInMs)
        }
        mediaMetadataRetriever.release()
    }

    private fun initTransFormer() {

        trans = Transformer.Builder(this@VideoEditActivity)
            .setVideoMimeType(MimeTypes.VIDEO_H265)
            .setAudioMimeType(MimeTypes.AUDIO_AAC)
            .build()

        trans.addListener(object : Listener {
            override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                super.onCompleted(composition, exportResult)
                viewModel.finishLoading()
                intent.putExtra("path", file.path)
                intent.putExtra("original", viewModel.uri.value)
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

    private fun collectViewModelData() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                launch {
                    viewModel.recentState.collect{
                        if(!isPlaying()) {
                            moveSeekPositionMs(position = it )
                        }
                    }
                }
                launch {
                    viewModel.event.collect{event ->
                        when(event){
                            VideoEditEvent.OnPlayButtonClicked -> {
                                if(!isPlaying()) {
                                    startPlayer()
                                }else{
                                    stopPlayer()
                                }
                            }

                            VideoEditEvent.StopPlayer -> {
                                stopPlayer()
                            }

                            VideoEditEvent.OnBackButtonClicked -> {
                                finish()
                            }
                            VideoEditEvent.OnUploadWithoutEditButtonClicked -> {
                                viewModel.startLoading()
                                startTransformationWithoutEdit()
                            }

                            VideoEditEvent.OnCheckButtonClicked -> {
                                viewModel.startLoading()
                                startTransformation()
                            }
                        }

                    }
                }
            }
        }
    }

    private fun startTransformationWithoutEdit() {
        trans.start(mediaItem, file.path)
    }

    private fun startPlayer() {
        val player = binding.pv.player?: return
        if(viewModel.recentState.value < viewModel.rightThumbState.value){
            player.play()
            viewModel.playerIsPlaying(player.isPlaying)
            lifecycleScope.launch {
                while(isPlaying()){
                    if(viewModel.recentState.value >= viewModel.rightThumbState.value){
                        stopPlayer()
                        break
                    }
                    delay(100)
                    viewModel.updateRecent(binding.pv.player!!.currentPosition)
                }
            }
        }

    }

    private fun stopPlayer() {
        val player = binding.pv.player?: return
        player.pause()
        viewModel.playerIsPlaying(player.isPlaying)
    }

    private fun moveSeekPositionMs(position: Long) {
        binding.pv.player?.seekTo(position)
    }
    private fun isPlaying(): Boolean {
        val player = binding.pv.player?: return false
        return player.isPlaying
    }

    private fun initBinding() {
        with(binding){
            vm = viewModel
            pv.player = ExoPlayer.Builder(this@VideoEditActivity).build().also {
                it.setMediaItem(mediaItem)
                it.prepare()
            }
            pv.useController = false

            tlv.doOnLayout {
                viewModel.updateTLVSize(it.width, it.height)
            }
        }

    }

    private fun startTransformation() {
        val result = MediaItem.Builder()
            .setUri(viewModel.uri.value.toUri())
            .setClippingConfiguration(
                ClippingConfiguration.Builder()
                    .setStartPositionMs(viewModel.leftThumbState.value)
                    .setEndPositionMs(viewModel.rightThumbState.value)
                    .build()
            )
            .build()

        trans.start(result, file.path)
    }

    private fun getIntentExtra() {
        val uri = intent.getStringExtra("uri")?:""
        viewModel.setUri(uri)
    }

}