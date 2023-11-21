package com.boostcampwm2023.snappoint.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.CircleCropTransformation


suspend fun Context.getSnapPointFromUri(uri: String): Bitmap{
        val request = ImageRequest.Builder(this)
            .data(uri)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .size(100)
            .transformations(CircleCropTransformation())
            .build()
        val result = ImageLoader(this).execute(request).drawable
        return (result as BitmapDrawable).bitmap
    }

    fun Context.getFocusedSnapPointFromUri(){

    }
