package com.softhouse.workingout.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Running(
    var steps: Int,
)  {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}