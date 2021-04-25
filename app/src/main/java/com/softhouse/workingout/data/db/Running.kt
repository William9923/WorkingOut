package com.softhouse.workingout.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Running(
    var steps: Int,
    @ColumnInfo(name = "start_workout") var startWorkout: Long,
    @ColumnInfo(name = "end_workout") var endWorkout: Long
)  {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}