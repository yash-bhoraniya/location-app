package com.example.practicalappdev.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "address")

data class Address(
    var placeId: String,
    var address: String,
    var latitude: Double,
    var longitude:Double,
    var city:String,
    var isPrimary:Boolean,
    var distance:Double,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    ):Serializable
