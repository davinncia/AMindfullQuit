package com.davinciapp.amindfullquit.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.davinciapp.amindfullquit.meditation.MeditationSession

@Dao
interface MediationDao {

    @Query("SELECT * FROM meditation_table")
    fun getSessions(): LiveData<List<MeditationSession>>

    @Insert
    suspend fun insertSession(session: MeditationSession)

    //TODO delete
}