package com.boostcampwm2023.snappoint.presentation.main

import android.content.Context
import com.boostcampwm2023.snappoint.presentation.util.drawNumberOnSnapPoint
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class SnapPointClusterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<SnapPointClusterItem>
) : DefaultClusterRenderer<SnapPointClusterItem>(context, map, clusterManager) {

    init {
        clusterManager.renderer = this
    }

    override fun onBeforeClusterItemRendered(
        item: SnapPointClusterItem,
        markerOptions: MarkerOptions
    ) {
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(item.getIcon()))
    }

    override fun onBeforeClusterRendered(
        cluster: Cluster<SnapPointClusterItem>,
        markerOptions: MarkerOptions
    ) {
        val snapPointWithNumber = drawNumberOnSnapPoint(cluster.items.random().getIcon(), cluster.size)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(snapPointWithNumber))
    }

    override fun onClusterUpdated(cluster: Cluster<SnapPointClusterItem>, marker: Marker) {
        val snapPointWithNumber = drawNumberOnSnapPoint(cluster.items.random().getIcon(), cluster.size)
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(snapPointWithNumber))
    }
}