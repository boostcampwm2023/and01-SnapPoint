package com.boostcampwm2023.snappoint.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= 28) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}

fun resizeBitmap(bitmap: Bitmap, layoutWidth: Int): Bitmap {
    val height = bitmap.height * layoutWidth / bitmap.width
    return Bitmap.createScaledBitmap(bitmap, layoutWidth, height, false)
}

fun Bitmap.toByteArray(): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 99, byteArrayOutputStream)
    } else {
        compress(Bitmap.CompressFormat.WEBP, 99, byteArrayOutputStream)
    }
    return byteArrayOutputStream.toByteArray()
}