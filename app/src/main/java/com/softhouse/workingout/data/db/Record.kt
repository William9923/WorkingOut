package com.softhouse.workingout.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softhouse.workingout.ui.sensor.tracker.Mode

@Entity(tableName = "record_table")
data class Record(
    var recordId: Int,
    var mode: Mode,
    @ColumnInfo(name = "start_workout") var startWorkout: Long,
    @ColumnInfo(name = "end_workout") var endWorkout: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
