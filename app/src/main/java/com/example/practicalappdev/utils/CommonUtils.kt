package com.example.practicalappdev.utils

import android.app.AlertDialog
import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.practicalappdev.R

object  CommonUtils {
    fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
        val originLocation = Location("Origin")
        originLocation.latitude = lat1
        originLocation.longitude = long1

        val destinationLocation = Location("Destination")
        destinationLocation.latitude = lat2
        destinationLocation.longitude = long2


        val distanceInMeters = originLocation.distanceTo(destinationLocation)

        return distanceInMeters / 1000.0
    }


}