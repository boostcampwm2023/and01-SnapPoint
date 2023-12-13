package com.boostcampwm2023.snappoint.presentation.markerpointselector

import android.location.Geocoder
import android.os.Bundle
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityMapsMarkerBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.runBlocking
import java.util.Locale

class MarkerPointSelectorActivity : BaseActivity<ActivityMapsMarkerBinding>(R.layout.activity_maps_marker),
    OnMapReadyCallback
{
    private var startLatLng = LatLng(37.3586926, 127.1051209)
    private var index: Int = 0

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
        intent.putExtra("latitude", _map?.cameraPosition?.target?.latitude)
        intent.putExtra("longitude", _map?.cameraPosition?.target?.longitude)
    }

    private fun getIntentExtra() {
        intent.getDoubleArrayExtra("position")?.let{
            startLatLng = LatLng(it[0], it[1])
        }
        index = intent.getIntExtra("index",0)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        _map = googleMap

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 17.5f))
    }

    override fun onDestroy() {
        super.onDestroy()

        _map = null
    }

    private fun getAddressLine(): String {
        val geocoder = Geocoder(applicationContext, Locale.KOREA)
        val map: GoogleMap = _map ?: return ""
        val position: LatLng = map.cameraPosition.target

        val address = runBlocking { geocoder.getFromLocation(position.latitude, position.longitude, 1) }
        if (address.isNullOrEmpty()) return ""
        return address[0].getAddressLine(0)
    }
}