package com.boostcampwm2023.snappoint.presentation.main

import android.content.Context
import com.boostcampwm2023.snappoint.presentation.util.Constants.MIN_CLUSTER_SIZE
import com.boostcampwm2023.snappoint.presentation.util.drawNumberOnSnapPoint
import com.boostcampwm2023.snappoint.presentation.util.getSnapPointBitmap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SnapPointClusterRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<SnapPointClusterItem>,
    private val mapManager: MapManager
) : DefaultClusterRenderer<SnapPointClusterItem>(context, map, clusterManager) {

    private var prevSelectedPosition: LatLng? = null
    private var prevSelectedSize: Int? = null

    init {
        clusterManager.renderer = this
        minClusterSize = MIN_CLUSTER_SIZE
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
        val bitmap = cluster.items.find { it.position == cluster.position }?.getIcon() ?: return
        val snapPointWithNumber = drawNumberOnSnapPoint(bitmap, cluster.size)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(snapPointWithNumber))
    }

    override fun onClusterUpdated(cluster: Cluster<SnapPointClusterItem>, marker: Marker) {
        val url = cluster.items.find { it.position == cluster.position }?.getContent() ?: return
        CoroutineScope(Dispatchers.Default).launch {
            val bitmap = if (prevSelectedPosition == cluster.position && prevSelectedSize == cluster.size) {
                mapManager.setPrevSelectedCluster(cluster)
                getSnapPointBitmap(context, url, true)
            } else {
                getSnapPointBitmap(context, url, false)
            }
            val snapPointWithNumber = drawNumberOnSnapPoint(bitmap, cluster.size)

            withContext(Dispatchers.Main) {
                marker.runCatching {
                    setIcon(BitmapDescriptorFactory.fromBitmap(snapPointWithNumber))
                }
            }
        }
    }

    fun setPrevSelected(cluster: Cluster<SnapPointClusterItem>?) {
        prevSelectedPosition = cluster?.position
        prevSelectedSize = cluster?.size
    }
}