package com.example.amindfullquit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.amindfullquit.meditation.MeditationSession
import com.example.amindfullquit.smoking.SmokingData

@Database(entities = [MeditationSession::class, SmokingData::class], version = 1)
abstract class MindfulRoomDatabase : RoomDatabase() {

    abstract fun smokingDao(): SmokingDao
    abstract fun meditationDao(): MediationDao

    companion object{
        //Singleton
        @Volatile
        private var INSTANCE: MindfulRoomDatabase? = null

        fun getDatabase(context: Context): MindfulRoomDatabase{
            if (INSTANCE == null){
                synchronized(this){
                    if (INSTANCE == null){
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext, MindfulRoomDatabase::class.java, "mindful_database")
                            .addMigrations(MIGRATION_1_2)
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        //MIGRATIONS
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //Migration schema
            }
        }
    }



}