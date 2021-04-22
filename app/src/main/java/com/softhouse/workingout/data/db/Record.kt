package com.softhouse.workingout.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softhouse.workingout.ui.sensor.tracker.Mode

@Entity(tableName = "record_table")
data class Record(
    @PrimaryKey var recordId: Int = 0,
    @PrimaryKey var mode: String = Mode.STEPS.toString(),
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
