package com.example.amindfullquit.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.amindfullquit.repository.PreferencesRepository
import com.example.amindfullquit.settings.SettingsViewModel
import com.example.amindfullquit.smoking.SmokingDataViewModel


class ViewModelFactory private constructor(private val application: Application): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmokingDataViewModel::class.java)) {
            return SmokingDataViewModel(PreferencesRepository.getInstance(application)) as T
        } else if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(PreferencesRepository.getInstance(application)) as T
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