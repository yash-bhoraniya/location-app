package com.example.practicalappdev.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import com.example.practicalappdev.R
import com.example.practicalappdev.models.Address
import com.example.practicalappdev.databinding.ActivityRouteBinding
import com.example.practicalappdev.networking.api.RouteApi
import com.example.practicalappdev.networking.repository.DirectionsRepository
import com.example.practicalappdev.roomdb.LoggerLocalDataSource
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.practicalappdev.networking.api.Result
import com.example.practicalappdev.networking.models.Leg
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil

@AndroidEntryPoint
class RouteActivity : AppCompatActivity(), OnMapReadyCallback,View.OnClickListener{

    private lateinit var binding: ActivityRouteBinding

    @Inject
    lateinit var logger: LoggerLocalDataSource
    lateinit var apiKey:String

    private val directionsRepository = DirectionsRepository(RouteApi.apiService)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {

        setView()
        apiKey = resources.getString(R.string.google_maps_api_key)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        clickEvent()
    }
    private fun clickEvent() {
        binding.includeHeader.ivLocation.setOnClickListener(this)
    }

    private fun setView() {

        binding.includeHeader.imgFilter.isInvisible = true
        binding.includeHeader.ivLocation.setImageResource(R.drawable.baseline_arrow_back_24)
        binding.includeHeader.tvTitle.text = resources.getString(R.string.routing)


    }


    override fun onMapReady(map: GoogleMap) {
        showRoutes(map)
    }



    private fun showRoutes(map: GoogleMap) {
        logger.getAddressList {address ->

             val destinations = mutableListOf<LatLng>()

            for (i in 0 until address.size){
                destinations.add(i, LatLng(address[i].latitude, address[i].longitude) )
            }

            val origin = "${destinations.first().latitude},${destinations.first().longitude}"
            val destination = "${destinations.last().latitude},${destinations.last().longitude}"
            val waypoints = destinations.subList(1, destinations.size - 1)
                .joinToString("|") { "${it.latitude},${it.longitude}" }


            lifecycleScope.launch {
                directionsRepository.getDirections(origin, destination, waypoints, apiKey)
                    .collect { result ->
                        when (result) {
                            is Result.Success -> {
                                val directionsResponse = result.data

                                val route = directionsResponse.routes.firstOrNull()
                                val legs = route?.legs.orEmpty()

                                drawRoute(legs, map)
                                addMarker(address, map)
                                zoomToRoute(legs, map)


                            }

                            is Result.Error -> {
                                val exception = result.exception

                            }

                            is Result.Loading -> {
                            }
                        }
                    }
            }
        }

    }

    private fun addMarker(address: MutableList<Address>, map: GoogleMap) {
        for (latLng in address){
            val markerOptions = MarkerOptions().position(LatLng(latLng.latitude, latLng.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(latLng.city)
            map.addMarker(markerOptions)
        }

    }

    private fun drawRoute(legs: List<Leg>, map: GoogleMap) {
        for (leg in legs) {
            for (step in leg.steps) {
                val points = PolyUtil.decode(step.polyline.points)
                val polylineOptions = PolylineOptions().addAll(points).color(Color.RED)
                map.addPolyline(polylineOptions)
            }
        }
    }

    private fun zoomToRoute(legs: List<Leg>, map: GoogleMap) {
        val boundsBuilder = LatLngBounds.builder()

        val points = mutableListOf<LatLng>()
        for (leg in legs) {
            for (step in leg.steps) {
                val stepPoints = PolyUtil.decode(step.polyline.points)
                points.addAll(stepPoints)
            }
        }

        for (point in points) {
            boundsBuilder.include(point)
        }

        val bounds = boundsBuilder.build()


        val padding = 100
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        map.animateCamera(cameraUpdate)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_location->{
                finish()
            }
        }
    }

}