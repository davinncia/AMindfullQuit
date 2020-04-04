package com.example.amindfullquit.meditation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//Date format = YYMMDD
@Entity(tableName = "meditation_table")
data class MeditationSession(
    @PrimaryKey
    @ColumnInfo(name = "creation_time")
    val creationTimeStamp: Long,

    @ColumnInfo(name = "minutes")
    var minutes: Int = 0)