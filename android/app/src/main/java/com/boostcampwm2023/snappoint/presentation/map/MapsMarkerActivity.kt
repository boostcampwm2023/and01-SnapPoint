package com.boostcampwm2023.snappoint.presentation.map

import android.os.Bundle
import android.util.Log
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityMapsMarkerBinding
import com.boostcampwm2023.snappoint.presentation.Position
import com.boostcampwm2023.snappoint.presentation.PostBlock
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsMarkerActivity : BaseActivity<ActivityMapsMarkerBinding>(R.layout.activity_maps_marker),
    OnMapReadyCallback {

    private var _post: PostBlock = PostBlock.IMAGE("Content", Position(37.3586926, 127.1051209))
    private var _marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val map: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fcv_google_map) as SupportMapFragment
        map.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
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
                .draggable(true)
        )
        _marker?.tag = post

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f))
    }

    override fun onStop() {
        super.onStop()

        val post: PostBlock = _marker?.tag as? PostBlock ?: return
        val marker: Marker = _marker!!
        val newPosition: Position = Position(marker.position.latitude, marker.position.longitude)

        _post = when(post) {
            is PostBlock.IMAGE -> PostBlock.IMAGE(post.content, newPosition)
            is PostBlock.VIDEO -> PostBlock.IMAGE(post.content, newPosition)
            else -> return
        }

        Log.d("LOG", "NEW POSITION: $newPosition")
    }

    override fun onDestroy() {
        super.onDestroy()

        _marker = null
    }
}