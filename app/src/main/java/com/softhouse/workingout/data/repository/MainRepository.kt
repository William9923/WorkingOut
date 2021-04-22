package com.softhouse.workingout.data.repository

import androidx.lifecycle.LiveData
import com.softhouse.workingout.data.db.*
import com.softhouse.workingout.ui.sensor.tracker.Mode
import java.util.*
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runningDao: RunningDao,
    val recordDao: RecordDao,
    val cyclingDao: CyclingDao
) {
    // Insert
    suspend fun insertCycling(cycling: Cycling) = cyclingDao.insertCycling(cycling)
    suspend fun insertRunning(running: Running) = runningDao.insertRunning(running)
    suspend fun insertRecord(record: Record) = recordDao.insertRecord(record)

    // Delete
    suspend fun deleteCycling(cycling: Cycling) = cyclingDao.deleteCycling(cycling)
    suspend fun deleteRunning(running: Running) = runningDao.deleteRunning(running)
    suspend fun deleteRecord(record: Record) = recordDao.deleteRecord(record)

    // Query Specific Result
    fun getAllRecordBasedOnMode(mode: Mode) = recordDao.getAllRecordBasedOnMode(mode.toString())
    fun getAllRecordBasedOnDate(start: Date, end: Date) = recordDao.getAllRecordBasedOnDate(start.time, end.time)
    fun getAllCyclingSortedByDistanceInMeter() = cyclingDao.getAllCyclingSortedByDistanceInMeter()
    fun getAllRunningSortedBySteps() = runningDao.getAllRunningSortedBySteps()

    // Query One Specific Result
    fun getSpecificRecordById(id: Int) = recordDao.getSpecificRecordById(id)
    fun getSpecificCyclingById(id: Int) = cyclingDao.getSpecificCyclingById(id)
    fun getSpecificRunningById(id: Int) = runningDao.getSpecificRunningById(id)

    // Query Aggregated Result
    fun getTotalDistance() = cyclingDao.getTotalDistance()
    fun getTotalSteps() = runningDao.getTotalSteps()

}