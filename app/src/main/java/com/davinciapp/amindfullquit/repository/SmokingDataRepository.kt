package com.davinciapp.amindfullquit.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.davinciapp.amindfullquit.database.MindfulRoomDatabase
import com.davinciapp.amindfullquit.database.SmokingDao
import com.davinciapp.amindfullquit.smoking.SmokingData

class SmokingDataRepository(application: Application) {

    private var smokingDao: SmokingDao = MindfulRoomDatabase.getDatabase(application).smokingDao()

    val allData: LiveData<List<SmokingData>> = smokingDao.getSmokingData()

    suspend fun insert(data: SmokingData){
        smokingDao.insertSmokingData(data)
    }

    suspend fun update(data: SmokingData) {
        smokingDao.updateData(data)
    }

    suspend fun deleteDataBefore(timeStamp: Long) {
        smokingDao.deleteDataBefore(timeStamp)
    }

    suspend fun clearData() {
        smokingDao.clearData()
    }


    companion object {
        private var INSTANCE: SmokingDataRepository? = null

        fun getInstance(application: Application): SmokingDataRepository {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) INSTANCE = SmokingDataRepository(application)
                }
            }
            return INSTANCE!!
        }
    }
}