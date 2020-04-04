package com.example.amindfullquit.repository

import android.app.Application
import com.example.amindfullquit.database.MindfulRoomDatabase
import com.example.amindfullquit.meditation.MeditationSession

class MeditationSessionRepository(application: Application) {

    private val meditationDao = MindfulRoomDatabase.getDatabase(application).meditationDao()

    val allSessions = meditationDao.getSessions()

    suspend fun insertSession(session: MeditationSession) = meditationDao.insertSession(session)



    companion object {
        private var INSTANCE: MeditationSessionRepository? = null

        fun getInstance(application: Application): MeditationSessionRepository {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) INSTANCE = MeditationSessionRepository(application)
                }
            }
            return INSTANCE!!
        }
    }

}