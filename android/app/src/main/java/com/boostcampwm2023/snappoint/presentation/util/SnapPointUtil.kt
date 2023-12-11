package com.boostcampwm2023.snappoint.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.scale
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.presentation.util.Constants.CLUSTER_TEXT_SIZE

private val clusterTextSize = CLUSTER_TEXT_SIZE.pxFloat()
private val clusterCircleRadius = (CLUSTER_TEXT_SIZE / 2 + 1).pxFloat()

val snapPointHeight = 100.px()
val snapPointWidth = 85.px()

suspend fun getSnapPointBitmap(context: Context, uri: String, focused: Boolean): Bitmap {
    val request = ImageRequest.Builder(context)
        .data(uri)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .transformations(CircleCropTransformation())
        .size(200.px())
        .build()
    val result = ImageLoader(context).execute(request).drawable ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    val userImage = (result as BitmapDrawable).bitmap.scale(width = 69.px(), height = 69.px())
    val container = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.icon_snap_point_container
    ).scale(width = 74.px(), height = 74.px())
    val snapPointUnFocused = BitmapFactory.decodeResource(
        context.resources,
        if(focused) {
            R.drawable.icon_snap_point_focused
        } else {
            R.drawable.icon_snap_point_unfocused
        }
    ).scale(width = snapPointWidth, height = snapPointHeight)

    return mergeToSnapPointBitmap(listOf(snapPointUnFocused, container, userImage))
}

fun mergeToSnapPointBitmap(bitmaps: List<Bitmap>): Bitmap {
    val result = Bitmap.createBitmap(bitmaps[0].width, bitmaps[0].height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val paint = Paint()
    bitmaps.forEach {
        canvas.drawBitmap(it, bitmaps[0].width.toFloat() / 2 - it.width.toFloat() / 2, bitmaps[0].width.toFloat() / 2 - it.width.toFloat() / 2, paint)
    }
    return result
}

fun drawNumberOnSnapPoint(bitmap: Bitmap, number: Int) : Bitmap {
    val result = Bitmap.createBitmap(bitmap)
    val canvas = Canvas(result)
    val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = clusterTextSize
    }
    val circlePaint = Paint().apply {
        color = Color.RED
        textAlign = Paint.Align.CENTER
    }
    val yPos = clusterCircleRadius - (textPaint.descent() + textPaint.ascent()) / 2
    canvas.drawCircle(clusterCircleRadius, clusterCircleRadius, clusterCircleRadius, circlePaint)
    canvas.drawText(number.toString(), clusterCircleRadius / 2, yPos, textPaint)
    return result
}