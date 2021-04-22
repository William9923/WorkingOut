package com.softhouse.workingout.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

typealias Polyline = MutableList<LatLng>

@Entity(tableName = "cycling_table")
data class Cycling(
    var timestamp: Long = 0L,
    var distanceInMeters: Int = 0,
    var points: Polyline = mutableListOf()
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
