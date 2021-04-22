package com.softhouse.workingout.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record_table")
data class Record(
    var recordId: Int,
    var mode: String,
    @ColumnInfo(name = "start_workout") var startWorkout: Long,
    @ColumnInfo(name = "end_workout") var endWorkout: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
