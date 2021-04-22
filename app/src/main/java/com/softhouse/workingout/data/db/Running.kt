package com.softhouse.workingout.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Running(
    var timestamp: Long = 0L,
    var steps: Int = 0,
)  {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}