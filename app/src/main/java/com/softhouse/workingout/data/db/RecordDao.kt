package com.softhouse.workingout.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.softhouse.workingout.ui.sensor.tracker.Mode


@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: Record) : Long

    @Delete
    suspend fun deleteRecord(record: Record)

    @Query("SELECT * FROM record_table")
    fun getAllRecord(): LiveData<List<Record>>

    @Query("SELECT * FROM record_table WHERE id = :id LIMIT 1")
    fun getSpecificRecordById(id: Int): LiveData<Record>

    @Query("SELECT * FROM record_table WHERE start_workout BETWEEN :dayStart AND :dayEnd")
    fun getAllRecordBasedOnDate(dayStart: Long, dayEnd: Long): LiveData<List<Record>>

    @Query("SELECT * FROM record_table WHERE mode LIKE (:mode)")
    fun getAllRecordBasedOnMode(mode: String): LiveData<List<Record>>

}