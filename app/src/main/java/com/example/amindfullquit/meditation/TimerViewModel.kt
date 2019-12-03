package com.example.amindfullquit.meditation

import androidx.lifecycle.ViewModel
import java.util.*

class TimerViewModel : ViewModel(){

    private var mMinutes: Int = 0

    fun addMinute(){
        mMinutes += 1;
    }

    fun saveSession(){
        val session = MeditationSession(Calendar.getInstance().time, mMinutes)
        //Save with Room
    }
}