package com.boostcampwm2023.snappoint.presentation.map

import android.os.Bundle
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityMapsMarkerBinding
import com.boostcampwm2023.snappoint.presentation.Position
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsMarkerActivity : BaseActivity<ActivityMapsMarkerBinding>(R.layout.activity_maps_marker),
    OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val map: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fcv_google_map) as SupportMapFragment
        map.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val naver = LatLng(37.3586926, 127.1051209)
        googleMap.addMarker(
            MarkerOptions()
                .position(naver)
                .title("NAVER 1784")
                .draggable(true)
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(naver, 17.5f))
    }
}