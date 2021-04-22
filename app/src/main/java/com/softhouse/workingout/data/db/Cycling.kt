package com.softhouse.workingout.data.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cycling_table")
data class Cycling(
    var distanceInMeters: Int,
    var points: Polyline
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
