package com.softhouse.workingout.data.db

import androidx.room.TypeConverter
import com.softhouse.workingout.ui.sensor.tracker.Mode

class Converters {

    @TypeConverter
    fun toModeDatabase(mode: Mode): String {
        return when (mode) {
            Mode.STEPS -> "STEPS"
            Mode.CYCLING -> "CYCLING"
        }
    }

    @TypeConverter
    fun toModeApplication(mode: String): Mode {
        return when (mode) {
            Mode.STEPS.toString() -> Mode.STEPS
            Mode.CYCLING.toString() -> Mode.CYCLING
            else -> throw Error("Converted : No Mode detected")
        }
    }
}