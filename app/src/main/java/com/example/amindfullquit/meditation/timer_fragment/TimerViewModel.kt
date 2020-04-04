package com.example.amindfullquit.meditation.timer_fragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amindfullquit.meditation.MeditationSession
import com.example.amindfullquit.repository.MeditationSessionRepository
import kotlinx.coroutines.launch
import java.util.*

class TimerViewModel(application: Application, private val meditationRepo: MeditationSessionRepository)
    : AndroidViewModel(application){

    private var minutes: Int = 0

    fun saveSession(){
        viewModelScope.launch {
            meditationRepo.insertSession(MeditationSession(System.currentTimeMillis(), minutes))
        }
    }

    fun setMeditationLength(min: Int) {
        minutes = min
    }

    fun addAMinute() {
        minutes += 1;
    }
}