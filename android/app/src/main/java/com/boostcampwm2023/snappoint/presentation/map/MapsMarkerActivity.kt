package com.boostcampwm2023.snappoint.presentation.map

import android.os.Bundle
import android.util.Log
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityMapsMarkerBinding
import com.boostcampwm2023.snappoint.presentation.createpost.Position
import com.boostcampwm2023.snappoint.presentation.createpost.PostBlock
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsMarkerActivity : BaseActivity<ActivityMapsMarkerBinding>(R.layout.activity_maps_marker),
    OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener {

    private var _post: PostBlock = PostBlock.IMAGE("Content", Position(37.3586926, 127.1051209))
    private var _marker: Marker? = null
    private var _map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val map: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fcv_google_map) as SupportMapFragment
        map.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        _map = googleMap

        val post = _post
        val latLng = when (post) {
            is PostBlock.VIDEO -> LatLng(post.position.latitude, post.position.longitude)
            is PostBlock.IMAGE -> LatLng(post.position.latitude, post.position.longitude)
            else -> return
        }

        _marker = googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(post.content)
                .draggable(false)
        )
        _marker?.tag = post

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f))

        googleMap.setOnCameraMoveListener(this)
        googleMap.setOnCameraIdleListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        _marker = null
        _map = null
    }

    override fun onCameraMove() {
        val map: GoogleMap = _map ?: return
        moveMarker(map.cameraPosition.target)
    }

    override fun onCameraIdle() {
        val map: GoogleMap = _map ?: return
        updateBlockPosition(map.cameraPosition.target)
    }

    private fun moveMarker(latLng: LatLng) {
        val marker: Marker = _marker ?: return
        marker.position = latLng
    }

    private fun updateBlockPosition(latLng: LatLng) {
        val marker: Marker = _marker ?: return
        val tag: PostBlock = (marker.tag as? PostBlock) ?: return
        val newPosition: Position = Position(latLng.latitude, latLng.longitude)

        marker.tag = when (tag) {
            is PostBlock.IMAGE -> tag.copy(tag.content, newPosition)
            is PostBlock.VIDEO -> tag.copy(tag.content, newPosition)
            else -> return
        }
    }
}