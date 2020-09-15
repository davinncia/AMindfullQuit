package com.davinciapp.amindfullquit.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davinciapp.amindfullquit.developer.DeveloperViewModel
import com.davinciapp.amindfullquit.meditation.log_fragment.LogViewModel
import com.davinciapp.amindfullquit.meditation.timer_fragment.TimerViewModel
import com.davinciapp.amindfullquit.repository.MeditationSessionRepository
import com.davinciapp.amindfullquit.repository.PreferencesRepository
import com.davinciapp.amindfullquit.repository.SmokingDataRepository
import com.davinciapp.amindfullquit.settings.SettingsViewModel
import com.davinciapp.amindfullquit.smoking.SmokingDataViewModel


class ViewModelFactory private constructor(private val application: Application): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(SmokingDataViewModel::class.java) -> {
                return SmokingDataViewModel(
                    PreferencesRepository.getInstance(application),
                    SmokingDataRepository.getInstance(application)) as T

            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                return SettingsViewModel(
                    PreferencesRepository.getInstance(application),
                    SmokingDataRepository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(LogViewModel::class.java) -> {
                return LogViewModel(
                    application,
                    MeditationSessionRepository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(TimerViewModel::class.java) -> {
                return TimerViewModel(
                    application,
                    MeditationSessionRepository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(DeveloperViewModel::class.java) -> {
                return DeveloperViewModel(application) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
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