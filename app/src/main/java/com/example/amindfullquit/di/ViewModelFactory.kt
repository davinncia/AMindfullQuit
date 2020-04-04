package com.example.amindfullquit.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.amindfullquit.meditation.log_fragment.LogViewModel
import com.example.amindfullquit.meditation.timer_fragment.TimerViewModel
import com.example.amindfullquit.repository.MeditationSessionRepository
import com.example.amindfullquit.repository.PreferencesRepository
import com.example.amindfullquit.repository.SmokingDataRepository
import com.example.amindfullquit.settings.SettingsViewModel
import com.example.amindfullquit.smoking.SmokingDataViewModel
import java.util.*


class ViewModelFactory private constructor(private val application: Application): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmokingDataViewModel::class.java)) {
            return SmokingDataViewModel(
                PreferencesRepository.getInstance(application),
                SmokingDataRepository.getInstance(application)) as T

        } else if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                PreferencesRepository.getInstance(application),
                SmokingDataRepository.getInstance(application)) as T
        } else if (modelClass.isAssignableFrom(LogViewModel::class.java)) {
            return LogViewModel(
                application,
                MeditationSessionRepository.getInstance(application)) as T
        } else if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            return TimerViewModel(
                application,
                MeditationSessionRepository.getInstance(application)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object{
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory{
            if (INSTANCE == null){
                synchronized(ViewModelFactory){
                    if (INSTANCE == null) {
                        INSTANCE = ViewModelFactory(application)
                    }
                }
            }
            return INSTANCE!!
        }

    }
}