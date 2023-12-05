package com.boostcampwm2023.snappoint.presentation.main

import android.graphics.Bitmap
import com.boostcampwm2023.snappoint.presentation.model.SnapPointTag
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class SnapPointClusterItem(
    private val position: LatLng,
    private val title: String,
    private val snippet: String,
    private val tag: SnapPointTag,
    private val icon: Bitmap
) : ClusterItem {
    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    fun getTag(): SnapPointTag {
        return tag
    }

    fun getIcon(): Bitmap {
        return icon
    }
}