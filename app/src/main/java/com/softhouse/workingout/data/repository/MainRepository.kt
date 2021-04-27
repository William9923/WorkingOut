package com.softhouse.workingout.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.softhouse.workingout.data.db.*
import com.softhouse.workingout.shared.DateTimeUtility
import com.softhouse.workingout.ui.sensor.tracker.Mode
import java.util.*
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runningDao: RunningDao,
    val cyclingDao: CyclingDao
) {

    // Insert
    suspend fun insertCycling(cycling: Cycling) = cyclingDao.insertCycling(cycling)
    suspend fun insertRunning(running: Running) = runningDao.insertRunning(running)

    // Query All Data
    fun getAllRunningRecord() = runningDao.getAllRunningRecord()
    fun getAllCyclingRecord() = cyclingDao.getAllCyclingRecord()

    // Query based on date:
    fun getAllCyclingRecordBasedOnDate(day: Int, month: Int, year: Int): LiveData<List<Cycling>> {
        val calender = DateTimeUtility.getCalender(day, month, year)
        val nextDay = DateTimeUtility.getCalender(day, month, year)
        nextDay.add(Calendar.DAY_OF_MONTH, 1)
        return cyclingDao.getAllCyclingBasedOnDate(calender.timeInMillis, nextDay.timeInMillis)
    }

    fun getAllRunningRecordBasedOnDate(day: Int, month: Int, year: Int): LiveData<List<Running>> {
        val calender = DateTimeUtility.getCalender(day, month, year)
        val nextDay = DateTimeUtility.getCalender(day, month, year)
        nextDay.add(Calendar.DAY_OF_MONTH, 1)
        Log.d("Repository", "Start : ${calender.timeInMillis}")
        Log.d("Repository", "End : ${nextDay.timeInMillis}")
        return runningDao.getAllRunningBasedOnDate(calender.timeInMillis, nextDay.timeInMillis)
    }

    // Query One Specific Result
    fun getSpecificCyclingById(id: Long) = cyclingDao.getSpecificCyclingById(id)
    fun getSpecificRunningById(id: Long) = runningDao.getSpecificRunningById(id)

    // Query Aggregated Result
    fun getTotalDistance() = cyclingDao.getTotalDistance()
    fun getTotalSteps() = runningDao.getTotalSteps()
}