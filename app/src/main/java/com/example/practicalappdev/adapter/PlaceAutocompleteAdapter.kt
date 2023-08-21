package com.example.practicalappdev.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.practicalappdev.R
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class PlaceAutocompleteAdapter(
    context: Context,
    private val placesClient: PlacesClient
) : ArrayAdapter<AutocompletePrediction>(context, android.R.layout.simple_dropdown_item_1line) {

    private val filter = PlaceFilter()

    override fun getFilter(): Filter {
        return filter
    }

    private inner class PlaceFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(constraint.toString())
                .setSessionToken(AutocompleteSessionToken.newInstance())
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { autocompletePredictions ->
                    results.values = autocompletePredictions.autocompletePredictions
                    results.count = autocompletePredictions.autocompletePredictions.size

                    Log.e("===", "performFiltering: ${autocompletePredictions.autocompletePredictions}", )
                    publishResults(constraint, results)
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }

            return results
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return if (resultValue is AutocompletePrediction) {
                resultValue.getFullText(null)
            } else {
                super.convertResultToString(resultValue)
            }
        }


        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clear()
            if (results?.count ?: 0 > 0) {
                addAll(results?.values as List<AutocompletePrediction>)
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_dropdown_item, parent, false)
        val prediction = getItem(position) as AutocompletePrediction
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = prediction.getFullText(null)
        return view
    }
}

