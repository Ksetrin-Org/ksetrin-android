package org.ksetrin.ksetrin.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.PlacesSearchResult
import com.google.maps.model.RankBy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ksetrin.ksetrin.R


class ShopsFragment : Fragment() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val callback = OnMapReadyCallback { googleMap ->
        setResults(googleMap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shops, container, false)
    }

    private fun setupMap(){
        val mapFragment = childFragmentManager.findFragmentById(R.id.shopsFragmentMap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setResults(googleMap: GoogleMap) {

        coroutineScope.launch {
            accessLocation { location ->
                val results = getSearchResults(location)!!
                for (i in results){
                    val place = LatLng(i.geometry.location.lat, i.geometry.location.lng)
                    googleMap.addMarker(MarkerOptions().position(place).title(i.name))
                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 14F))
            }
        }
    }

    private fun getSearchResults(location: Location): Array<out PlacesSearchResult>? {
        val context: GeoApiContext = GeoApiContext.Builder()
            .apiKey(getString(R.string.google_api_key))
            .build()
        val loc : com.google.maps.model.LatLng = com.google.maps.model.LatLng(location.latitude, location.longitude)
        val request = PlacesApi.nearbySearchQuery(context, loc)
            .radius(4000)
            .rankby(RankBy.PROMINENCE)
            .keyword("seeds")
            .language("en")
            .await()
        return request.results
    }

    @SuppressLint("MissingPermission")
    private suspend fun accessLocation(function: (it: Location) -> Unit) {
        if (!isPermissionPresent()) {
            requestMyPermissions()
        }
        if (isPermissionPresent()) {
            val mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            mFusedLocationClient.lastLocation
                .addOnSuccessListener {
                    function(it)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error getting location", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun isPermissionPresent(): Boolean {
        val permission1 = Manifest.permission.ACCESS_FINE_LOCATION
        val permission2 = Manifest.permission.ACCESS_COARSE_LOCATION
        val res1 = requireContext().checkCallingOrSelfPermission(permission1)
        val res2 = requireContext().checkCallingOrSelfPermission(permission2)
        return res1 == PackageManager.PERMISSION_GRANTED && res2 == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun requestMyPermissions() {
        permissionsBuilder(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).build().sendSuspend()
    }

}