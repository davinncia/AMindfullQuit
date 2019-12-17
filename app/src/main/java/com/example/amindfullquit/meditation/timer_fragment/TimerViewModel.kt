package com.example.amindfullquit.meditation.timer_fragment

import androidx.lifecycle.ViewModel
import com.example.amindfullquit.meditation.MeditationSession
import java.util.*

class TimerViewModel : ViewModel(){

    private var mMinutes: Int = 0

    fun addMinute(){
        mMinutes += 1;
    }

    fun saveSession(){

        //Save with Room
    }
}