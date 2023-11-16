package com.boostcampwm2023.snappoint.presentation.map

import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentMapsMarkerBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.createpost.PositionState
import com.boostcampwm2023.snappoint.presentation.createpost.PostBlockState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapsMarkerFragment : BaseFragment<FragmentMapsMarkerBinding>(R.layout.fragment_maps_marker),
    OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener {

    private var _post: PostBlockState =
        PostBlockState.IMAGE("Content", Uri.EMPTY, PositionState(37.3586926, 127.1051209))
    private var _marker: Marker? = null
    private var _map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val map: SupportMapFragment =
            getParentFragmentManager()
                .findFragmentById(R.id.fcv_google_map_fr) as SupportMapFragment
        map.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        _map = googleMap

        val post = _post
        val latLng = when (post) {
            is PostBlockState.VIDEO -> LatLng(post.position.latitude, post.position.longitude)
            is PostBlockState.IMAGE -> LatLng(post.position.latitude, post.position.longitude)
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

        binding.btnFindPlaceFr.setOnClickListener {
            Log.d("LOG", getAddressLine())
        }
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
            is PostBlockState.IMAGE -> tag.copy(tag.content, tag.uri, newPositionState)
            is PostBlockState.VIDEO -> tag.copy(tag.content, newPositionState)
            else -> return
        }
    }

    private fun getAddressLine(): String {
        val geocoder = Geocoder(requireContext(), Locale.KOREA)
        val marker: Marker = _marker ?: return ""
        val position: LatLng = marker.position

        geocoder.getFromLocation(position.latitude, position.longitude, 1)?.let {
            return it[0].getAddressLine(0)
        }
        return ""
    }
}
