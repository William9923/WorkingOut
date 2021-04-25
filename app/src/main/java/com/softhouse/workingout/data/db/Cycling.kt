package com.softhouse.workingout.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cycling_table")
data class Cycling(
    var distanceInMeters: Int,
    var points: Polyline,
    @ColumnInfo(name = "start_workout") var startWorkout: Long,
    @ColumnInfo(name = "end_workout") var endWorkout: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}
