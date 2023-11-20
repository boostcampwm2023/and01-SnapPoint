package com.boostcampwm2023.snappoint.presentation

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityMainBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main),
    OnMapReadyCallback
{
    private var _map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bnv.setupWithNavController(navController)

        val map: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fcv_main_map) as SupportMapFragment
        map.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        _map = googleMap

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(0.0,0.0), 17.5f))
    }


}