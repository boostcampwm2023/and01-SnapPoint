package com.boostcampwm2023.snappoint.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult


    suspend fun Context.getSnapPointFromUri(uri: String): Bitmap{
        val request = ImageRequest.Builder(this).data(uri).size(50).build()
        val result = (ImageLoader(this).execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    fun Context.getFocusedSnapPointFromUri(){

    }
