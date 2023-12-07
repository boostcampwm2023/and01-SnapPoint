package com.boostcampwm2023.snappoint.presentation.main

import android.graphics.Bitmap
import com.boostcampwm2023.snappoint.presentation.model.SnapPointTag
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class SnapPointClusterItem(
    private val position: LatLng,
    private val tag: SnapPointTag,
    private val content: String,
    private val icon: Bitmap
) : ClusterItem {
    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return ""
    }

    override fun getSnippet(): String {
        return ""
    }

    fun getTag(): SnapPointTag {
        return tag
    }

    fun getContent(): String {
        return content
    }

    fun getIcon(): Bitmap {
        return icon
    }
}