package com.davinciapp.amindfullquit.settings

import android.util.Log
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davinciapp.amindfullquit.repository.PreferencesRepository
import com.davinciapp.amindfullquit.repository.SmokingDataRepository
import kotlinx.coroutines.launch
import java.util.*

class SettingsViewModel(
    private val preferencesRepo: PreferencesRepository,
    private val smokingRepo: SmokingDataRepository
) : ViewModel() {

    //Cigarettes smoked in a day
    val defaultConsumption = preferencesRepo.defaultConsumptionLiveData()

    //{year, month, day}
    val startingDateArray = Transformations.map(preferencesRepo.startingTimeLiveData()) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = it
        return@map arrayOf(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
    }


    val previousConsumption = preferencesRepo.previousConsumptionLiveData()
    val cigarettesPerPack = preferencesRepo.cigarettesPerPackLiveData()
    val packPrice = preferencesRepo.packPriceLiveData()
    val currency = preferencesRepo.currencyLiveData()


    fun updateDefaultCigarettesNumber(i: Int) {
        preferencesRepo.updateDefaultConsumption(i)
    }

    fun updateStartingTime(year: Int, month: Int, day: Int) {
        val startCalendar = GregorianCalendar(year, month, day)
        preferencesRepo.updateStartingTime(startCalendar.timeInMillis)

        deleteDataBefore(year, month, day)
        //clearData()
    }

    fun updatePreviousCigarettesNumber(i: Int) {
        preferencesRepo.updatePreviousConsumption(i)
    }

    fun updateCigarettesNumberPerPack(i: Int) {
        preferencesRepo.updateCigarettesPerPack(i)
    }

    fun updatePackPrice(i: Float) {
        preferencesRepo.updatePackPrice(i)
    }

    fun updateCurrency(symbol: String) {
        preferencesRepo.updateCurrency(symbol)
    }

    //DB
    private fun clearData() {
        viewModelScope.launch {
            smokingRepo.clearData()
        }
    }

    private fun deleteDataBefore(year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, month, day)
        val minTimeStamp = cal.timeInMillis
        viewModelScope.launch {
            smokingRepo.deleteDataBefore(minTimeStamp)
        }

    }
}