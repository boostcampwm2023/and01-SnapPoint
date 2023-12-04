package com.boostcampwm2023.snappoint.presentation.main

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.model.SnapPointTag
import com.boostcampwm2023.snappoint.presentation.util.addImageMarker
import com.boostcampwm2023.snappoint.presentation.util.pxFloat
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

class MapManager(private val viewModel: MainViewModel, private val context: Context):
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    var googleMap: GoogleMap? = null
        private set


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

        googleMap.setOnMarkerClickListener(this)

        viewModel.onMapReady()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        viewModel.onMarkerClicked(marker.tag as SnapPointTag)
        return true
    }

    fun removeFocus() {
        prevSelectedMarker?.remove()
        drawnRoute?.remove()
        prevSelectedIndex = -1
    }

    fun changeRoute(postBlocks: List<PostBlockState>) {
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
        postState.forEachIndexed { postIndex, postSummaryState ->
            SnapPointState(
                index = postIndex,
                markers = postSummaryState.postBlocks.filterIsInstance<PostBlockState.IMAGE>().mapIndexed { pointIndex, postBlockState ->
                    googleMap?.addImageMarker(
                        context = context,
                        markerOptions = MarkerOptions().position(postBlockState.position.asLatLng()),
                        uri = postBlockState.content,
                        tag = SnapPointTag(postIndex = postIndex, snapPointIndex = pointIndex),
                        focused = false
                    )
                }
            )
        }
    }


}