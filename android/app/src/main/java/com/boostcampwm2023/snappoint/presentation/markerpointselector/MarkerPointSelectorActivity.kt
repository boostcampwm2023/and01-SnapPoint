package com.boostcampwm2023.snappoint.presentation.markerpointselector

import android.location.Geocoder
import android.os.Bundle
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityMapsMarkerBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.boostcampwm2023.snappoint.presentation.model.PositionState
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MarkerPointSelectorActivity : BaseActivity<ActivityMapsMarkerBinding>(R.layout.activity_maps_marker),
    OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener
{
    private var startLatLng = LatLng(37.3586926, 127.1051209)
    private var index: Int = 0

    private var _marker: Marker? = null
    private var _map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getIntentExtra()

        val map: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fcv_google_map) as SupportMapFragment
        map.getMapAsync(this)

        binding.btnConfirmPosition.setOnClickListener {
            setIntentExtra()
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun setIntentExtra() {
        intent.putExtra("address", getAddressLine())
        intent.putExtra("index", index)
        intent.putExtra("latitude", _marker?.position?.latitude)
        intent.putExtra("longitude", _marker?.position?.longitude)
    }

    private fun getIntentExtra() {
        intent.getDoubleArrayExtra("position")?.let{
            startLatLng = LatLng(it[0], it[1])
        }
        index = intent.getIntExtra("index",0) ?: 0
    }

    override fun onMapReady(googleMap: GoogleMap) {
        _map = googleMap

        _marker = googleMap.addMarker(
            MarkerOptions()
                .position(startLatLng)
                .title("this is title")
                .draggable(false)
        )
        _marker?.tag = "this is tag"

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 17.5f))

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
        val tag: PostBlockState = (marker.tag as? PostBlockState) ?: return
        val newPositionState: PositionState = PositionState(latLng.latitude, latLng.longitude)

        marker.tag = when (tag) {
            is PostBlockState.IMAGE -> tag.copy(tag.content, tag.uri, position = newPositionState)
            is PostBlockState.VIDEO -> tag.copy(tag.content, tag.uri, position = newPositionState)
            else -> return
        }
    }

    private fun getAddressLine(): String {
        val geocoder = Geocoder(applicationContext, Locale.KOREA)
        val marker: Marker = _marker ?: return ""
        val position: LatLng = marker.position

        geocoder.getFromLocation(position.latitude, position.longitude, 1)?.let {
            return it[0].getAddressLine(0)
        }
        return ""
    }
}