package com.example.amindfullquit.smoking

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//Rmq: we are saving only days with custom (not default) cigarette nbr

@Entity(tableName = "smoking_table")
data class SmokingData (
    @PrimaryKey
    @ColumnInfo(name = "creation_time")
    val creationTimeStamp: Long,

    @ColumnInfo(name = "quantity")
    val cigaretteNbr: Int)