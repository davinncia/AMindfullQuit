package com.example.amindfullquit.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PreferencesRepository private constructor(context: Context) {

    companion object {
        private var INSTANCE: PreferencesRepository? = null

        fun getInstance(context: Context): PreferencesRepository{
            if (INSTANCE == null){
                synchronized(PreferencesRepository){
                    if (INSTANCE == null){
                        INSTANCE = PreferencesRepository(context)
                    }
                }
            }
            return INSTANCE!!
        }

        private const val SMOKING_FILE = "smoking_file"
        private const val START_TIME_KEY = "start_time_key"
        private const val DEFAULT_CONSUMPTION = "default_consumption"
        private const val PREVIOUS_CONSUMPTION = "previous_consumption"
    }

    private val smokingPrefs = context.getSharedPreferences(SMOKING_FILE, Context.MODE_PRIVATE)

    private val defaultConsumption = MutableLiveData<Int>()
    private val startingTime = MutableLiveData<Long>()
    private val previousConsumption = MutableLiveData<Int>()

    //Forcing strong reference (weak by default)
    private var prefListener: SharedPreferences.OnSharedPreferenceChangeListener

    init {
        defaultConsumption.value = smokingPrefs.getInt(DEFAULT_CONSUMPTION, 0)
        startingTime.value = smokingPrefs.getLong(START_TIME_KEY, System.currentTimeMillis())
        previousConsumption.value = smokingPrefs.getInt(PREVIOUS_CONSUMPTION, 0)

        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                START_TIME_KEY -> startingTime.value = smokingPrefs.getLong(START_TIME_KEY, System.currentTimeMillis())
                DEFAULT_CONSUMPTION -> defaultConsumption.value = smokingPrefs.getInt(DEFAULT_CONSUMPTION, 0)
                PREVIOUS_CONSUMPTION -> previousConsumption.value = smokingPrefs.getInt(PREVIOUS_CONSUMPTION, 0)
            }
        }

        smokingPrefs.registerOnSharedPreferenceChangeListener(prefListener)
    }

    fun startingTimeLiveData(): LiveData<Long> = startingTime
    fun defaultConsumptionLiveData(): LiveData<Int> = defaultConsumption
    fun previousConsumptionLiveData(): LiveData<Int> = previousConsumption

    fun updateStartingTime(timeStamp: Long){
        smokingPrefs.edit().putLong(START_TIME_KEY, timeStamp).apply()
    }

    fun updateDefaultConsumption(quantity: Int){
        smokingPrefs.edit().putInt(DEFAULT_CONSUMPTION, quantity).apply()
    }

    fun updatePreviousConsumption(quantity: Int){
        smokingPrefs.edit().putInt(PREVIOUS_CONSUMPTION, quantity).apply()
    }
}