package com.boostcampwm2023.snappoint.presentation.videoedit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class ThumbnailView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {



    private lateinit var uri: Uri

    private var viewModel : VideoEditViewModel? = null
    private var videoUri: Uri? = null
    private val thumbnails: MutableList<Bitmap> = mutableListOf()
    private var viewWidth = 0F
    private var videoLengthInMs = 0F
    private var secDivideTenX = 0F

    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }


    private fun getBitMap() {
        secDivideTenX =  viewWidth * 100 / videoLengthInMs
    }


    fun setViewModel(viewModel: VideoEditViewModel){
        this.viewModel = viewModel
        this.uri = viewModel.uri.value.toUri()

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
        this.uri = uri.toUri()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

    }


}