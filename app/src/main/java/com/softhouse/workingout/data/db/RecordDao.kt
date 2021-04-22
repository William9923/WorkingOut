package com.softhouse.workingout.data.db

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: Record)

    @Delete
    suspend fun deleteRecord(record: Record)

    @Query("SELECT * FROM record_table")
    fun getAllRecord(): LiveData<List<Record>>

    @Query("SELECT * FROM record_table WHERE mode LIKE 'Cycling'")
    fun getAllCyclingRecord(): LiveData<List<Record>>

    @Query("SELECT * FROM record_table WHERE mode LIKE 'Steps'")
    fun getAllRunningRecord(): LiveData<List<Record>>
}