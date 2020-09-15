package com.davinciapp.amindfullquit.meditation.timer_fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.davinciapp.amindfullquit.meditation.MeditationSession
import com.davinciapp.amindfullquit.repository.MeditationSessionRepository
import kotlinx.coroutines.launch

class TimerViewModel(application: Application, private val meditationRepo: MeditationSessionRepository)
    : AndroidViewModel(application){

    private var minutes: Int = 0

    fun saveSession(){
        if (minutes > 0) {
            viewModelScope.launch {
                meditationRepo.insertSession(MeditationSession(System.currentTimeMillis(), minutes))
            }
        }
    }

    fun setMeditationLength(min: Int) {
        minutes = min
    }

    fun addAMinute() {
        minutes += 1;
    }
}