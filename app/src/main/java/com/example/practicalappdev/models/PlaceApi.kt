package com.example.practicalappdev.models

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class PlaceApi {
    fun autoComplete(input: String): ArrayList<String?>? {
        val arrayList: ArrayList<String?> = ArrayList()
        var connection: HttpURLConnection? = null
        val jsonResult = StringBuilder()
        try {
            val sb = StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?")
            sb.append("input=$input")
            sb.append("&key=AIzaSyBSNyp6GQnnKlrMr7hD2HGiyF365tFlK5U")
            val url = URL(sb.toString())
            connection = url.openConnection() as HttpURLConnection
            val inputStreamReader = InputStreamReader(connection.inputStream)
            var read: Int
            val buff = CharArray(1024)
            while (inputStreamReader.read(buff).also { read = it } != -1) {
                jsonResult.append(buff, 0, read)
            }
            Log.d("JSon", jsonResult.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
        try {
            val jsonObject = JSONObject(jsonResult.toString())
            val prediction = jsonObject.getJSONArray("predictions")
            for (i in 0 until prediction.length()) {
                arrayList.add(prediction.getJSONObject(i).getString("description"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return arrayList
    }
}