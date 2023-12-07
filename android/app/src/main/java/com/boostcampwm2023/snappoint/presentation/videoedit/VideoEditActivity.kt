package com.boostcampwm2023.snappoint.presentation.videoedit

import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType
import java.io.File

@AndroidEntryPoint
class VideoEditActivity : BaseActivity<ActivityVideoEditBinding>(R.layout.activity_video_edit) {

    private val viewModel: VideoEditViewModel by viewModels()

    private var index = 0

    @OptIn(UnstableApi::class) override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getIntentExtra()

        initBinding()

        collectViewModelData()


        val mediaItem = MediaItem.fromUri(viewModel.uri.value.toUri())
        val mediaMetadataRetriever = MediaMetadataRetriever().apply {
            setDataSource(this@VideoEditActivity, viewModel.uri.value.toUri())
        }
        val trans = Transformer.Builder(this).setVideoMimeType(MimeTypes.VIDEO_H265).setAudioMimeType(MimeTypes.AUDIO_AAC).build()



        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER)
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
            //binding.pv.player?.seekTo(0)
            val file = createExternalCacheFile("asdf.mp4")
            trans.addListener(object : Listener{

                override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                    super.onCompleted(composition, exportResult)
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
                    Log.d("TAG", "onError: ${exportResult}")
                    Log.d("TAG", "onError: ${exportException}")
                }
            })
            trans.start(mediaItem, file.path)

        }

    }
    private fun createExternalCacheFile(fileName: String): File {
        val  file = File(externalCacheDir, fileName);
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
        return file

    }

    private fun collectViewModelData() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                launch {
                    viewModel.leftThumbState.collect{ position ->
                        moveSeekPositionMs(position = position)
                    }
                }
                launch {
                    viewModel.rightThumbState.collect{position ->
                        moveSeekPositionMs(position = position)
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
        }
    }

    private fun getIntentExtra() {
        val uri = intent.getStringExtra("uri")?:""
        viewModel.setUri(uri)
        index = intent.getIntExtra("index",0) ?: 0
    }

}