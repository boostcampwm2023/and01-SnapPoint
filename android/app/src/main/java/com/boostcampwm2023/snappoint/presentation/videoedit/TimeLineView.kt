package com.boostcampwm2023.snappoint.presentation.videoedit

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.FloatRange
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import kotlin.math.abs

class TimeLineView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {


    private lateinit var uri: Uri

    private var viewModel : VideoEditViewModel? = null
    private var viewWidth = 0F
    private var leftRect = RectF()
    private var leftPosX = 0F
    private var rightRect = RectF()
    private var rightPosX = 0F
    private var videoLengthInMs = 0F
    private var secDivideTenX = 0F

    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }

    init{
        initListener()
    }

    private fun getBitMap() {
        secDivideTenX =  viewWidth * 100 / videoLengthInMs
        leftMoved(timeToPosition(viewModel?.leftThumbState?.value!!))
        rightMoved(timeToPosition(viewModel?.rightThumbState?.value!!))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        var before = ""
        var recent = 0F

        setOnTouchListener { _, event ->
            val x = event.x
            if(x < 0 || x > viewWidth) {
                before = ""
                return@setOnTouchListener true
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    recent = x
                    before = if(leftPosX <= x && leftPosX+30 >= x){
                        "L"
                    }else if(rightPosX-30 <= x && rightPosX >= x){
                        "R"
                    }else{
                        ""
                    }
                    val time = positionToTime(x)
                    viewModel?.updateRecent(time)
                }

                MotionEvent.ACTION_MOVE -> {
                    when{
                        before == "L"-> {
                            if(x+60 > rightPosX) {
                                leftMoved(rightPosX - 60)
                                return@setOnTouchListener true
                            }
                        }
                        before == "R"-> {
                            if(leftPosX+60 > x) {
                                rightMoved(leftPosX+60)
                                return@setOnTouchListener true
                            }
                        }
                    }

                    when(before){
                        "L" ->{
                            leftMoved(x)
                            if(x - recent > 0 && abs(x - recent) > secDivideTenX){
                                recent = x
                                viewModel?.onLeftThumbMoved(positionToTime(x))
                            }else if(recent - x > 0 && abs(x - recent) > secDivideTenX){
                                recent = x
                                viewModel?.onLeftThumbMoved(positionToTime(x))
                            }
                        }
                        "R" ->{
                            rightMoved(x)
                            if(x - recent > 0 && abs(x - recent) > secDivideTenX){
                                recent = x
                                viewModel?.onRightThumbMoved(positionToTime(x))
                            }else if(recent - x > 0 && abs(x - recent) > secDivideTenX){
                                recent = x
                                viewModel?.onRightThumbMoved(positionToTime(x))
                            }
                        }
                        else ->{

                        }
                    }

                }
            }
            return@setOnTouchListener true
        }
    }

    private fun positionToTime(x: Float): Long {
        return (x / secDivideTenX * 100).toLong()
    }
    private fun timeToPosition(ms: Long): Float {
        return ms / 100 * secDivideTenX
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

    private fun leftMoved(posX: Float) {
        leftPosX = posX
        leftRect = RectF(posX,0F,posX+30F,100F)
        invalidate()
    }

    private fun rightMoved(posX: Float) {
        rightPosX = posX
        rightRect = RectF(posX-30F,0F,posX,100F)
        invalidate()
    }

    fun setUri(uri: String){
        this.uri = uri.toUri()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(rightRect,paint)
        canvas.drawRect(leftRect,paint)
    }

}