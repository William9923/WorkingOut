package com.softhouse.workingout.data.db

import androidx.room.TypeConverter
import com.softhouse.workingout.ui.sensor.tracker.Mode

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

typealias Polyline = MutableList<LatLng>

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

    @TypeConverter
    fun toLocation(locationString: String?): Polyline? {
        return try {
            Gson().fromJson(locationString, Array<LatLng>::class.java).toMutableList()
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun toLocationString(location: Polyline?): String? {
        return Gson().toJson(location)
    }
}