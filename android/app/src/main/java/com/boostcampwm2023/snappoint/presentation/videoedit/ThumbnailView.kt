package com.boostcampwm2023.snappoint.presentation.videoedit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import kotlin.math.ceil

class ThumbnailView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {




    private var viewModel : VideoEditViewModel? = null
    private var videoUri: Uri? = null
    private val thumbnails: MutableList<Bitmap> = mutableListOf()
    private var viewWidth = 0F
    private var viewHeight = 0F
    private var videoLengthInMs = 0F


    private fun getBitMap() {
        if (videoLengthInMs == 0F || viewWidth == 0F || viewHeight == 0F) return
        if(thumbnails.isNotEmpty()) return

        val mediaMetadataRetriever: MediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(context, videoUri)
        val initialBitmap = mediaMetadataRetriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        val frameHeight = viewHeight.toInt()
        val frameWidth = ((initialBitmap?.width?.toFloat()!! / initialBitmap?.height?.toFloat()!!) * frameHeight).toInt()
        val numThumbs = ceil(viewWidth / frameWidth).toInt()
        val interval = videoLengthInMs.toLong() / numThumbs
        for(i in 0 until numThumbs){
            var bitmap = mediaMetadataRetriever.getFrameAtTime(i * interval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            if(bitmap != null){
                bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth, frameHeight, false)
                thumbnails.add(bitmap)
            }
        }
        invalidate()
    }


    fun setViewModel(viewModel: VideoEditViewModel){
        this.viewModel = viewModel
        this.videoUri = viewModel.uri.value.toUri()

        collectViewModelData()
    }

    private fun collectViewModelData() {
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            findViewTreeLifecycleOwner()?.repeatOnLifecycle(Lifecycle.State.RESUMED){
                launch{
                    viewModel?.TLVWidth?.collect {
                        viewWidth = it
                        getBitMap()
                    }
                }
                launch{
                    viewModel?.TLVHeight?.collect {
                        viewHeight = it
                        getBitMap()
                    }
                }
                launch {
                    viewModel?.videoLengthInMs?.collect {
                        videoLengthInMs = it
                        getBitMap()
                    }
                }
            }
        }
    }

    fun setUri(uri: String){
        this.videoUri = uri.toUri()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var x = 0
        thumbnails.forEach { bitmap ->
            canvas.drawBitmap(bitmap, x.toFloat(), 0F, null)
            x += bitmap.width
        }

    }


}