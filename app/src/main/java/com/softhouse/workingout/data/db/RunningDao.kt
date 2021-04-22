package com.softhouse.workingout.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunningDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunning(running: Running)

    @Delete
    suspend fun deleteRunning(running: Running)

    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRunningSortedByDate(): LiveData<List<Running>>

    @Query("SELECT * FROM running_table ORDER BY steps DESC")
    fun getAllRunningSortedBySteps(): LiveData<List<Running>>

    @Query("SELECT SUM(steps) FROM running_table")
    fun getTotalSteps(): LiveData<Int>
}