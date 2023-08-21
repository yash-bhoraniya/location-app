package com.example.practicalappdev.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import com.example.practicalappdev.R
import com.example.practicalappdev.adapter.PlaceAutocompleteAdapter
import com.example.practicalappdev.models.Address
import com.example.practicalappdev.databinding.ActivitySearchBinding
import com.example.practicalappdev.roomdb.LoggerLocalDataSource
import com.example.practicalappdev.utils.CommonUtils
import com.example.practicalappdev.utils.KeyboardVisibilityHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject



@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var binding: ActivitySearchBinding
    private var googleMap: GoogleMap? = null
    private lateinit var placesClient: PlacesClient
    var marker: Marker? = null
    var position:Int =0
    var databasePlaceId = ""
    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityHelper

    @Inject
    lateinit var logger: LoggerLocalDataSource
    var popupWindow: PopupWindow?=null
    private var isUpdate = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        keyboardVisibilityHelper = KeyboardVisibilityHelper(this@SearchActivity.window.decorView.rootView)
        init()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ClickableViewAccessibility")
    private fun init() {


        setView()
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

//        mapFragment!!.getMapAsync(this)

        placesClient = Places.createClient(this@SearchActivity)

        val autocompleteAdapter = PlaceAutocompleteAdapter(this, placesClient)

        binding.autoCompleteTextView.setAdapter(autocompleteAdapter)

        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as AutocompletePrediction
            val placeId = item.placeId
            CommonUtils.hideKeyboard(this@SearchActivity, binding.autoCompleteTextView)
            // Call a function to retrieve detailed place information using the placeId
            fetchPlaceDetails(placeId,true)
        }

        if(intent.extras!=null){
            position = intent.getIntExtra("position",0)
            databasePlaceId = intent.getStringExtra("data").toString()
            isUpdate = true
            fetchPlaceDetails(intent.getStringExtra("data")!!, false)
        }

        keyboardVisibilityHelper.setKeyboardVisibilityListener { isVisible ->
            if (isVisible) {
                popupWindow?.dismiss()
            }
        }
        clickEvent()
    }

    private fun clickEvent() {
        binding.includeHeader.ivLocation.setOnClickListener(this)
    }

    private fun setView() {

            binding.includeHeader.imgFilter.isInvisible = true
            binding.includeHeader.ivLocation.setImageResource(R.drawable.baseline_arrow_back_24)
            binding.includeHeader.tvTitle.text = resources.getString(R.string.search_location)


    }

    private fun fetchPlaceDetails(placeId: String, isShowDialog: Boolean) {
        val fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS)

        val request = FetchPlaceRequest.builder(placeId, fields).build()

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            val placeName = place.name
            val placeAddress = place.address
            val latLng = place.latLng
            val placeId = place.id

            val addressComponents = place.addressComponents?.asList() ?: emptyList()
            for (component in addressComponents) {
                val types = component.types ?: emptyList()
                if (types.contains("locality")) {
                    val city = component.name
                    getMarkerOnMap(latLng!!, placeAddress, placeId, city, isShowDialog)
                    break
                }
            }




            // Now you can use the retrieved information as needed
            // For example, display it on the UI or perform other actions
        }.addOnFailureListener { exception ->
            // Handle error
        }


    }


    private fun getMarkerOnMap(
        latLng: LatLng,
        placeAddress: String?,
        placeId: String?,
        city: String,
        isShowDialog: Boolean
    ) {
        Log.e("===", "getMarkerOnMap: in  placeId :: $placeId")

//        parseAddress(placeDetails.address)



        val markerOptions = MarkerOptions().position(LatLng(latLng.latitude, latLng.longitude))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            .title(placeAddress)

        if (marker != null) {
            marker?.remove()
        }
        marker = googleMap?.addMarker(markerOptions)
        marker?.showInfoWindow()

        val cameraPosition =
            CameraPosition.Builder().target(LatLng(latLng.latitude, latLng.longitude))
                .zoom(15f) // Zoom level, can be adjusted
                .build()

//        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(placeDetails.lat, placeDetails.lng), 15f))
        googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))


        if(isShowDialog){

            showpopup(latLng, placeAddress, placeId,city)
        }

    }

    @SuppressLint("ServiceCast")
    private fun showpopup(latLng: LatLng, placeAddress: String?, placeId: String?, city: String) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.dialog_window, null)


         popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )
        /*val x = 0
        val y = resources.displayMetrics.heightPixels*/
//        popupWindow.isOutsideTouchable = false


        popupWindow?.showAtLocation(popupView, Gravity.BOTTOM, 0, 0)

        val textViewTitle = popupView.findViewById<AppCompatTextView>(R.id.dialogTitleTv)
        val buttonSave = popupView.findViewById<Button>(R.id.btn_save)

        logger.getAddressList { response ->
            if (response.any{ it.placeId == placeId }){
                textViewTitle.text = getString(R.string.do_you_want_to_update_the_selected_location)
                buttonSave.text = getString(R.string.update)
            }else {
                if (isUpdate){
                    textViewTitle.text = getString(R.string.do_you_want_to_update_the_selected_location)
                    buttonSave.text = getString(R.string.update)
                }else {
                    textViewTitle.text = getString(R.string.do_you_want_to_save_the_selected_location)
                    buttonSave.text = getString(R.string.save)
                }
            }
        }



        buttonSave.setOnClickListener {
            popupWindow?.dismiss()

            logger.getAddressList { response ->
                Log.e("===", "showpopup: ${response.size} $response", )
                var isPrimary = false
                if (response.isEmpty()) {
                    val newAddress =
                        Address(placeId.toString(), placeAddress!!, latLng.latitude, latLng.longitude, city,true, 0.0)
                    logger.addLog(newAddress)
                } else {
                    if (databasePlaceId.isNotEmpty()){
                        for (address in response){
                            if (address.placeId == databasePlaceId) {
                                address.placeId = placeId.toString()
                                address.address = placeAddress.toString()
                                address.latitude = latLng.latitude
                                address.longitude = latLng.longitude
                                address.city = city

                                if (address.isPrimary){
                                    address.distance = 0.0
                                    isPrimary = true
                                }else {
                                    address.distance =  CommonUtils.distance(response[0].latitude, response[0].longitude, latLng.latitude, latLng.longitude)
                                }

                                logger.editAddress(address)
                            }
                        }

                    } else {
                        if (response.any{ it.placeId == placeId }){
                            for (address in response){
                                if (address.placeId == placeId) {
                                    address.placeId = placeId.toString()
                                    address.address = placeAddress.toString()
                                    address.latitude = latLng.latitude
                                    address.longitude = latLng.longitude
                                    address.city = city
                                    address.distance =  CommonUtils.distance(response[0].latitude, response[0].longitude, latLng.latitude, latLng.longitude)
                                    logger.editAddress(address)
                                }
                            }

                        }else {
                            val newAddress = Address(
                                placeId.toString(),
                                placeAddress!!,
                                latLng.latitude,
                                latLng.longitude,
                                city,
                                false,
                                CommonUtils.distance(
                                    response[0].latitude, response[0].longitude,
                                    latLng.latitude,
                                        latLng.longitude
                                )
                            )
                            logger.addLog(newAddress)

                        }
                    }


                }

                val resultIntent = Intent()
                resultIntent.putExtra("isPrimary", isPrimary)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }


        }

    }



    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        Log.e("====", "onMapReady: $googleMap")
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_location->{
                finish()
            }
        }
    }
}