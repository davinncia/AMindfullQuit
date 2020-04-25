package com.example.amindfullquit.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.amindfullquit.smoking.SmokingData

@Dao
interface SmokingDao {

    @Query("SELECT * from smoking_table ORDER BY creation_time")
    fun getSmokingData(): LiveData<List<SmokingData>>

    @Insert
    suspend fun insertSmokingData(data: SmokingData)

    //UPDATE quantity
    @Update
    suspend fun updateData(data: SmokingData)

    //DELETE
    @Query("DELETE FROM smoking_table WHERE creation_time < :startingDate")
    suspend fun deleteDataBefore(startingDate: Long)

    @Query("DELETE FROM smoking_table")
    suspend fun clearData()

}