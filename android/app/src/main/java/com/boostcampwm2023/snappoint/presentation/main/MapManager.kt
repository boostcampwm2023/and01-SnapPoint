package com.boostcampwm2023.snappoint.presentation.main

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.model.SnapPointTag
import com.boostcampwm2023.snappoint.presentation.util.drawNumberOnSnapPoint
import com.boostcampwm2023.snappoint.presentation.util.getSnapPointBitmap
import com.boostcampwm2023.snappoint.presentation.util.pxFloat
import com.boostcampwm2023.snappoint.presentation.util.untilSixAfterDecimalPoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapManager(private val viewModel: MainViewModel, private val context: Context):
    OnMapReadyCallback,
//    GoogleMap.OnMarkerClickListener,
    ClusterManager.OnClusterItemClickListener<SnapPointClusterItem>,
    ClusterManager.OnClusterClickListener<SnapPointClusterItem> {

    var googleMap: GoogleMap? = null
        private set

    private lateinit var clusterManager: ClusterManager<SnapPointClusterItem>
    private lateinit var renderer: SnapPointClusterRenderer

    var prevSelectedCluster: SnapPointClusterItem? = null
//    var prevSelectedMarker: Marker? = null
    var drawnRoute: Polyline? = null
    var prevSelectedIndex = -1



    fun moveCamera(latitude: Double, longitude: Double, zoom: Float? = null) {
        zoom?.let {
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude),zoom))
        } ?: googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        clusterManager = ClusterManager(context, googleMap)
        renderer = SnapPointClusterRenderer(context, googleMap, clusterManager)
        googleMap.setOnCameraIdleListener(clusterManager)
        clusterManager.setOnClusterItemClickListener(this)
        clusterManager.setOnClusterClickListener(this)
//        googleMap.setOnMarkerClickListener(this)

        viewModel.onMapReady()
    }

//    override fun onMarkerClick(marker: Marker): Boolean {
//        viewModel.onMarkerClicked(marker.tag as SnapPointTag)
//        return true
//    }

    override fun onClusterItemClick(item: SnapPointClusterItem?): Boolean {
        clusterManager.removeItem(item)
        viewModel.onMarkerClicked(item?.getTag() ?: return false)
        return true
    }

    override fun onClusterClick(cluster: Cluster<SnapPointClusterItem>?): Boolean {
        if (cluster == null) return false

        renderer.getMarker(cluster).apply {
            CoroutineScope(Dispatchers.IO).launch {
                val selected = drawNumberOnSnapPoint(getSnapPointBitmap(context, cluster.items.random().getContent(), true), cluster.size)
                withContext(Dispatchers.Main) { setIcon(BitmapDescriptorFactory.fromBitmap(selected)) }
            }
        }
        return true
    }

    suspend fun removeFocus() {
//        prevSelectedMarker?.remove()
        drawnRoute?.remove()
        prevSelectedIndex = -1

        if (prevSelectedCluster != null) {
            val prevSelected = prevSelectedCluster ?: return
            clusterManager.removeItem(prevSelected)
            setItemUnfocused(prevSelected)
            prevSelectedCluster = null
            clusterManager.cluster()
        }
    }

    private suspend fun setItemUnfocused(selected: SnapPointClusterItem) {
        val bitmap = getSnapPointBitmap(context, selected.getContent(), false)
        clusterManager.addItem(SnapPointClusterItem(
            position = selected.position,
            tag = selected.getTag(),
            content = selected.getContent(),
            icon = bitmap
        ))
    }

    fun changeRoute(postBlocks: List<PostBlockState>) {
        prevSelectedIndex = viewModel.markerState.value.selectedIndex
        drawnRoute?.remove()

        val polylineOptions = PolylineOptions().color(getColor(context, R.color.error80)).width(3.pxFloat()).pattern(listOf(
            Dash(20f), Gap(20f)
        ))
        val positionList = postBlocks.filterNot { it is PostBlockState.TEXT }.map{ block ->
            when (block) {
                is PostBlockState.IMAGE -> {
                    LatLng(block.position.latitude, block.position.longitude)
                }

                is PostBlockState.VIDEO -> {
                    LatLng(block.position.latitude, block.position.longitude)
                }

                is PostBlockState.TEXT -> TODO()
            }
        }
        polylineOptions.addAll(positionList)

        drawnRoute = googleMap?.addPolyline(polylineOptions)
    }

    fun moveCamera(bound: LatLngBounds, padding: Int) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, padding))
    }

    suspend fun changeSelectedMarker(block: PostBlockState.IMAGE, snapPointTag: SnapPointTag) {
//        prevSelectedMarker?.remove()
//        prevSelectedMarker = googleMap?.addImageMarker(
//            context = context,
//            markerOptions = MarkerOptions().position(block.position.asLatLng()),
//            uri = block.content,
//            tag = snapPointTag,
//            focused = true
//        )

        if (prevSelectedCluster != null) {
            val prevSelected = prevSelectedCluster ?: return
            clusterManager.removeItem(prevSelected)
            setItemUnfocused(prevSelected)
        }
        val selectedBitmap = getSnapPointBitmap(context, block.content, true)
        prevSelectedCluster = SnapPointClusterItem(
            position = block.position.asLatLng(),
            tag = snapPointTag,
            content = block.content,
            icon = selectedBitmap
        )
        clusterManager.addItem(prevSelectedCluster)
        clusterManager.cluster()
    }

    suspend fun updateMarkers(postState: List<PostSummaryState>) {
        viewModel.startLoading()
//        googleMap?.clear()
        clusterManager.clearItems()
        postState.forEachIndexed { postIndex, postSummaryState ->
            postSummaryState.postBlocks.filterIsInstance<PostBlockState.IMAGE>()
                .forEachIndexed { pointIndex, postBlockState ->
//                    googleMap?.addImageMarker(
//                        context = context,
//                        markerOptions = MarkerOptions().position(postBlockState.position.asLatLng()),
//                        uri = postBlockState.content,
//                        tag = SnapPointTag(postIndex = postIndex, snapPointIndex = pointIndex),
//                        focused = false
//                    )
                    // cluster

                    val snapPoint = getSnapPointBitmap(context, postBlockState.content, false)
                    val clusterItem = SnapPointClusterItem(
                        position = postBlockState.position.asLatLng(),
                        tag = SnapPointTag(postIndex = postIndex, snapPointIndex = pointIndex),
                        content = postBlockState.content,
                        icon = snapPoint
                    )

                    clusterManager.addItem(clusterItem)
                }
        }
        clusterManager.cluster()
        viewModel.finishLoading()
    }

    fun searchSnapPoints() {
        val latLngBounds = googleMap?.projection?.visibleRegion?.latLngBounds ?: return

        val leftBottom = latLngBounds.southwest.latitude.untilSixAfterDecimalPoint().toString() +
                "," + latLngBounds.southwest.longitude.untilSixAfterDecimalPoint().toString()
        val rightTop = latLngBounds.northeast.latitude.untilSixAfterDecimalPoint().toString() +
                "," + latLngBounds.northeast.longitude.untilSixAfterDecimalPoint().toString()
        viewModel.loadPosts(leftBottom, rightTop)
    }
}