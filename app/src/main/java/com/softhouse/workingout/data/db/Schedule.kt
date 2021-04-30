package com.softhouse.workingout.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softhouse.workingout.ui.schedules.Types
import com.softhouse.workingout.ui.sensor.tracker.Mode

@Entity(tableName = "schedule_table")
data class Schedule(
    @ColumnInfo(name = "start_time") var startTime: Long,
    @ColumnInfo(name = "stop_time") var stopTime: Long,
    @ColumnInfo(name = "types") var types: Types,
    @ColumnInfo(name = "mode") var mode: Mode,
    @ColumnInfo(name = "date") var date: Long?,
    @ColumnInfo(name = "weeks") var weeks: List<String>,
    @ColumnInfo(name = "target") var target: Long,
    @ColumnInfo(name = "autoStart") var autoStart: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}