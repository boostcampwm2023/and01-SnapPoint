package com.boostcampwm2023.snappoint.presentation.util

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions


suspend fun GoogleMap.addImageMarker(context: Context, markerOptions: MarkerOptions, uri: String){

    val request = ImageRequest.Builder(context)
        .data(uri)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .size(100)
        .transformations(CircleCropTransformation())
        .build()
    val result = ImageLoader(context).execute(request).drawable

    this.addMarker(markerOptions.icon(
        BitmapDescriptorFactory.fromBitmap(
            (result as BitmapDrawable).bitmap
        )
    ))
}

fun Context.getFocusedSnapPointFromUri(){

}
