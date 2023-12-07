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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapManager(private val viewModel: MainViewModel, private val context: Context):
    OnMapReadyCallback,
    ClusterManager.OnClusterItemClickListener<SnapPointClusterItem>,
    ClusterManager.OnClusterClickListener<SnapPointClusterItem> {

    var googleMap: GoogleMap? = null
        private set

    private lateinit var clusterManager: ClusterManager<SnapPointClusterItem>
    private lateinit var renderer: SnapPointClusterRenderer

    var prevSelectedMarker: SnapPointClusterItem? = null
    var prevSelectedCluster: Cluster<SnapPointClusterItem>? = null
    var drawnRoute: Polyline? = null
    var prevSelectedIndex = -1

    private val markers: MutableList<Marker> = mutableListOf()

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

        viewModel.onMapReady()
    }

    override fun onClusterItemClick(item: SnapPointClusterItem?): Boolean {
        viewModel.onMarkerClicked(item?.getTag() ?: return false)
        return true
    }

    override fun onClusterClick(cluster: Cluster<SnapPointClusterItem>?): Boolean {
        if (cluster == null) return false

        setClusterUnfocused(prevSelectedCluster)

        prevSelectedCluster = cluster
        renderer.getMarker(cluster).apply {
            CoroutineScope(Dispatchers.IO).launch {
                val url = cluster.items.find { it.position == cluster.position }?.getContent() ?: return@launch
                val selected = drawNumberOnSnapPoint(getSnapPointBitmap(context, url, true), cluster.size)
                withContext(Dispatchers.Main) { setIcon(BitmapDescriptorFactory.fromBitmap(selected)) }
            }
        }
        viewModel.onClusterClicked(cluster.items.map { it.getTag() })
        return true
    }

    suspend fun removeFocus() {
        drawnRoute?.remove()
        prevSelectedIndex = -1

        if (prevSelectedMarker != null) {
            val prevSelected = prevSelectedMarker ?: return
            setItemUnfocused(prevSelected)
            prevSelectedMarker = null
            clusterManager.cluster()
        }

        setClusterUnfocused(prevSelectedCluster)
    }

    private suspend fun setItemUnfocused(selected: SnapPointClusterItem) {
        val bitmap = getSnapPointBitmap(context, selected.getContent(), false)
        clusterManager.markerCollection.markers.find { it.position == selected.position }?.setIcon(
            BitmapDescriptorFactory.fromBitmap(bitmap)
        )
    }

    private fun setClusterUnfocused(selected: Cluster<SnapPointClusterItem>?) {
        if (selected != null) {
            renderer.getMarker(selected).apply {
                if (this == null) return
                CoroutineScope(Dispatchers.IO).launch {
                    val url = selected.items.find { it.position == selected.position }?.getContent()
                        ?: return@launch
                    val bitmap = drawNumberOnSnapPoint(getSnapPointBitmap(context, url, false), selected.size)
                    withContext(Dispatchers.Main) {
                        setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    }
                }
            }
        }
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

        if (prevSelectedMarker != null) {
            val prevSelected = prevSelectedMarker ?: return
            setItemUnfocused(prevSelected)
        }
        val selectedBitmap = getSnapPointBitmap(context, block.content, true)
        clusterManager.markerCollection.markers.find { it.position == block.position.asLatLng() }?.setIcon(
            BitmapDescriptorFactory.fromBitmap(selectedBitmap)
        )
        prevSelectedMarker = SnapPointClusterItem(
            position = block.position.asLatLng(),
            tag = snapPointTag,
            content = block.content,
            icon = selectedBitmap
        )
        clusterManager.cluster()
    }

    suspend fun updateMarkers(postState: List<PostSummaryState>) {
        viewModel.startLoading()
        clusterManager.clearItems()
        postState.forEachIndexed { postIndex, postSummaryState ->
            postSummaryState.postBlocks.filterIsInstance<PostBlockState.IMAGE>()
                .forEachIndexed { pointIndex, postBlockState ->
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

    fun setZoomGesturesEnabled(boolean: Boolean) {
        googleMap?.uiSettings?.isZoomGesturesEnabled = boolean
    }

    fun setScrollGesturesEnabled(boolean: Boolean) {
        googleMap?.uiSettings?.isScrollGesturesEnabled = boolean
    }
}