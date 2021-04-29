package com.softhouse.workingout.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.softhouse.androidutils.Result
import com.softhouse.workingout.data.Alarm
import com.softhouse.workingout.data.db.AlarmDao

class AlarmRepository(private val alarmDao: AlarmDao) {

    fun observeAlarms(): LiveData<Result<List<Alarm>>> {
        return alarmDao.observeAlarms().map {
            Result.Success(it)
        }
    }

    suspend fun getAlarms(): Result<List<Alarm>> {
        return try {
            Result.Success(alarmDao.getAlarms())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getAlarmById(alarmId: Int): Result<Alarm> {
        return try {
            val alarm = alarmDao.getAlarmById(alarmId)
            if (alarm != null) {
                Result.Success(alarm)
            } else {
                Result.Error(Exception("Alarm not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun insertAlarm(alarm: Alarm) {
        alarmDao.insertAlarm(alarm)
    }

    suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm)
    }

    suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm)
    }

    suspend fun deleteAlarm(alarmId: Int) {
        alarmDao.deleteAlarmById(alarmId)
    }

    suspend fun disableAllAlarms() {
        alarmDao.disableAllAlarms()
    }

    suspend fun updateAlarm(alarmId: Int, enable: Boolean) {
        alarmDao.updateAlarmEnableStatus(alarmId, enable)
    }
}
