package com.boostcampwm2023.snappoint.presentation.videoedit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs

class TimeLineView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private lateinit var uri: Uri

    private var viewModel : VideoEditViewModel? = null
    private var leftRect = RectF()
    private var leftPosX = 0L
    private var rightRect = RectF()
    private var rightPosX = 1000L
    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }

    init{
        initListener()
    }

    private fun initListener() {
        var before = 1000F
        setOnTouchListener { _, event ->
            Log.d("TAG", "initListener: ${event.x} ${event.y}")
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                }

                MotionEvent.ACTION_MOVE -> {
                    if(abs(before - event.x) >= 5 ){
                        before = event.x
                        viewModel?.onRightThumbMoved(event.x.toLong())
                    }

                }

                MotionEvent.ACTION_UP -> {

                }
            }
            true
        }
    }

    fun setViewModel(viewModel: VideoEditViewModel){
        this.viewModel = viewModel
        collectViewModelData()
    }

    private fun collectViewModelData() {
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            findViewTreeLifecycleOwner()?.repeatOnLifecycle(Lifecycle.State.RESUMED){
                launch {
                    viewModel?.leftThumbState?.collect{
                        leftMoved(it)
                    }
                }
                launch {
                    viewModel?.rightThumbState?.collect{
                        rightMoved(it)
                    }
                }
            }

        }
    }

    private fun leftMoved(posX: Long) {
        leftPosX = posX
        leftRect = RectF(posX-10F,0F,posX+10F,100F)
        invalidate()
    }

    private fun rightMoved(posX: Long) {
        rightPosX = posX
        rightRect = RectF(posX-10F,0F,posX+10F,100F)
        invalidate()
    }

    fun setUri(uri: String){
        this.uri = uri.toUri()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(rightRect,paint)
        canvas.drawRect(leftRect,paint)
    }

}