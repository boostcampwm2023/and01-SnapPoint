package com.boostcampwm2023.snappoint.presentation.main

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.model.SnapPointTag
import com.boostcampwm2023.snappoint.presentation.util.addImageMarker
import com.boostcampwm2023.snappoint.presentation.util.getSnapPointBitmap
import com.boostcampwm2023.snappoint.presentation.util.pxFloat
import com.boostcampwm2023.snappoint.presentation.util.untilSixAfterDecimalPoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager

class MapManager(private val viewModel: MainViewModel, private val context: Context):
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    ClusterManager.OnClusterItemClickListener<SnapPointClusterItem>,
    ClusterManager.OnClusterClickListener<SnapPointClusterItem> {

    var googleMap: GoogleMap? = null
        private set

    private lateinit var clusterManager: ClusterManager<SnapPointClusterItem>

    var prevSelectedMarker: Marker? = null
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
        SnapPointClusterRenderer(context, googleMap, clusterManager)
        googleMap.setOnCameraIdleListener(clusterManager)
        clusterManager.setOnClusterItemClickListener(this)
//        googleMap.setOnMarkerClickListener(this)

        viewModel.onMapReady()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        viewModel.onMarkerClicked(marker.tag as SnapPointTag)
        return true
    }

    override fun onClusterItemClick(item: SnapPointClusterItem?): Boolean {
        viewModel.onMarkerClicked(item?.getTag() ?: return false)
        return true
    }

    override fun onClusterClick(cluster: Cluster<SnapPointClusterItem>?): Boolean {
        TODO("Not yet implemented")
    }

    fun removeFocus() {
        prevSelectedMarker?.remove()
        drawnRoute?.remove()
        prevSelectedIndex = -1
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
        prevSelectedMarker?.remove()
        prevSelectedMarker = googleMap?.addImageMarker(
            context = context,
            markerOptions = MarkerOptions().position(block.position.asLatLng()),
            uri = block.content,
            tag = snapPointTag,
            focused = true
        )

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
                        title = "",
                        snippet = "",
                        tag = SnapPointTag(postIndex = postIndex, snapPointIndex = pointIndex),
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