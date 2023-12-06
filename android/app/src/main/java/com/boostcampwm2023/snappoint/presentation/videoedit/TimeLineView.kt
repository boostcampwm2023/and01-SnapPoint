package com.boostcampwm2023.snappoint.presentation.videoedit

import android.content.Context
import android.graphics.Canvas
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class TimeLineView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private lateinit var uri: Uri

    private var onLeftThumbMoved: ((Long) -> Unit)? = null
    private var onRightThumbMoved: ((Long) -> Unit)? = null

    init{
        initListener()
    }

    private fun initListener() {
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                }

                MotionEvent.ACTION_MOVE -> {

                }

                MotionEvent.ACTION_UP -> {

                }
            }
            true
        }
    }

    fun setOnLeftThumbMovedListener(action: (Long) -> Unit){
        onLeftThumbMoved = action
    }

    fun setOnRightThumbMovedListener(action: (Long) -> Unit){
        onRightThumbMoved = action
    }

    fun setUri(uri: Uri){
        this.uri = uri
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

}