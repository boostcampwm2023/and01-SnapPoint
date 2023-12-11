package com.boostcampwm2023.snappoint.presentation.main

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat.getColor
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.model.SnapPointTag
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
import kotlinx.coroutines.delay
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

    private var prevSelectedMarker: SnapPointClusterItem? = null
    private var prevSelectedCluster: Cluster<SnapPointClusterItem>? = null
    private var drawnRoute: Polyline? = null
    var prevSelectedIndex = -1

    fun moveCamera(latitude: Double, longitude: Double, zoom: Float? = null) {
        zoom?.let {
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude),zoom))
        } ?: googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
    }

    override fun onMapReady(googleMap: GoogleMap) {

        clusterManager = ClusterManager(context, googleMap)
        renderer = SnapPointClusterRenderer(context, googleMap, clusterManager, this)
        googleMap.setOnCameraIdleListener(clusterManager)
        clusterManager.setOnClusterItemClickListener(this)
        clusterManager.setOnClusterClickListener(this)

        this.googleMap = googleMap
        viewModel.onMapReady()
    }

    override fun onClusterItemClick(item: SnapPointClusterItem?): Boolean {
        viewModel.onMarkerClicked(item?.getTag() ?: return false)
        return true
    }

    override fun onClusterClick(cluster: Cluster<SnapPointClusterItem>?): Boolean {
        if (cluster == null) return false
        if (cluster == prevSelectedCluster) return true

        setClusterUnfocused(prevSelectedCluster)

        prevSelectedCluster = cluster
        renderer.setPrevSelected(cluster)
        viewModel.onClusterClicked(cluster.items.map { it.getTag() })
        return true
    }

    suspend fun removeMarkerFocus() {
        drawnRoute?.remove()
        prevSelectedIndex = -1

        if (prevSelectedMarker != null) {
            setItemUnfocused(prevSelectedMarker)
            prevSelectedMarker = null
            clusterManager.cluster()
        }
    }

    fun removeClusterFocus() {
        renderer.setPrevSelected(null)
        setClusterUnfocused(prevSelectedCluster)
        prevSelectedCluster = null
    }

    private suspend fun setItemUnfocused(selected: SnapPointClusterItem?) {
        if (selected == null) return
        val bitmap = getSnapPointBitmap(context, selected.getContent(), false)
        renderer.getMarker(selected).apply {
            setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
            zIndex = 0.0f
        }
    }

    private fun setClusterUnfocused(selected: Cluster<SnapPointClusterItem>?) {
        if (selected != null) {
            renderer.getMarker(selected).apply {
                if (this == null) return
                renderer.setPrevSelected(null)
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

        setItemUnfocused(prevSelectedMarker)

        val selectedBitmap = getSnapPointBitmap(context, block.content, true)

        val item = clusterManager.algorithm.items.find { it.getTag() == snapPointTag }
        while (renderer.getMarker(item) == null) { delay(100) }
        renderer.getMarker(item).apply {
            zIndex = 1.0f
            setIcon(BitmapDescriptorFactory.fromBitmap(selectedBitmap))
        }
        prevSelectedMarker = item
    }

    suspend fun updateMarkers(postState: List<PostSummaryState>) {
        clusterManager.clearItems()
        postState.forEach { postSummaryState ->
            postSummaryState.postBlocks.filter { it !is PostBlockState.TEXT }
                .forEach { postBlockState ->
                    // cluster
                    lateinit var snapPoint: Bitmap
                    lateinit var clusterItem: SnapPointClusterItem
                    when(postBlockState){
                        is PostBlockState.IMAGE -> {
                            snapPoint = getSnapPointBitmap(context, postBlockState.url144P, false)
                            clusterItem = SnapPointClusterItem(
                                position = postBlockState.position.asLatLng(),
                                tag = SnapPointTag(postUuid = postSummaryState.uuid, blockUuid = postBlockState.uuid),
                                content = postBlockState.url144P,
                                icon = snapPoint
                            )

                        }
                        is PostBlockState.VIDEO -> {
                            snapPoint = getSnapPointBitmap(context, postBlockState.thumbnail144P, false)
                            clusterItem = SnapPointClusterItem(
                                position = postBlockState.position.asLatLng(),
                                tag = SnapPointTag(postUuid = postSummaryState.uuid, blockUuid = postBlockState.uuid),
                                content = postBlockState.thumbnail144P,
                                icon = snapPoint
                            )
                        }
                        else -> { }
                    }
                    clusterManager.addItem(clusterItem)
                }
        }
        clusterManager.cluster()
    }

    fun searchSnapPoints() {
        CoroutineScope(Dispatchers.IO).launch {
            val latLngBounds = withContext(Dispatchers.Main) {
                while (googleMap?.projection == null) { delay(100) }
                googleMap?.projection?.visibleRegion?.latLngBounds
            } ?: return@launch

            val leftBottom = latLngBounds.southwest.latitude.untilSixAfterDecimalPoint().toString() +
                    "," + latLngBounds.southwest.longitude.untilSixAfterDecimalPoint().toString()
            val rightTop = latLngBounds.northeast.latitude.untilSixAfterDecimalPoint().toString() +
                    "," + latLngBounds.northeast.longitude.untilSixAfterDecimalPoint().toString()
            viewModel.loadPosts(leftBottom, rightTop)
        }
    }

    fun setZoomGesturesEnabled(boolean: Boolean) {
        googleMap?.uiSettings?.isZoomGesturesEnabled = boolean
    }

    fun setScrollGesturesEnabled(boolean: Boolean) {
        googleMap?.uiSettings?.isScrollGesturesEnabled = boolean
    }

    fun setClusteringEnabled(boolean: Boolean) {
        if (googleMap == null) return
        renderer.minClusterSize = if (boolean) Int.MAX_VALUE else 2
        clusterManager.cluster()
    }

    fun setPrevSelectedCluster(prev: Cluster<SnapPointClusterItem>) {
        prevSelectedCluster = prev
    }
}