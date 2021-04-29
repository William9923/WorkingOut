package com.softhouse.workingout.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.softhouse.workingout.data.Alarm

@Database(
    entities = [Running::class, Cycling::class, Alarm::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getRunningDao(): RunningDao
    abstract fun getCyclingDao(): CyclingDao
    abstract fun getAlarmDao(): AlarmDao
}