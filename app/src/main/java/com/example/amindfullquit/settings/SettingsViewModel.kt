package com.example.amindfullquit.settings

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.amindfullquit.repository.PreferencesRepository
import java.util.*

class SettingsViewModel(private val preferencesRepo: PreferencesRepository) : ViewModel() {

    //Cigarettes smoked in a day
    val defaultConsumption = preferencesRepo.defaultConsumptionLiveData()

    //{year, month, day}
    val startingDateArray = Transformations.map(preferencesRepo.startingTimeLiveData()){
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = it
        return@map arrayOf(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
    }


    val previousConsumption = preferencesRepo.previousConsumptionLiveData()
    val cigarettesPerPack = preferencesRepo.cigarettesPerPackLiveData()
    val packPrice = preferencesRepo.packPriceLiveData()
    val currency = preferencesRepo.currencyLiveData()


    fun updateDefaultCigarettesNumber(i: Int){
        preferencesRepo.updateDefaultConsumption(i)
    }

    fun updateStartingTime(year: Int, month: Int, day: Int){
        val startCalendar = GregorianCalendar(year, month, day)
        preferencesRepo.updateStartingTime(startCalendar.timeInMillis)
    }

    fun updatePreviousCigarettesNumber(i: Int){
        preferencesRepo.updatePreviousConsumption(i)
    }

    fun updateCigarettesNumberPerPack(i: Int){
        preferencesRepo.updateCigarettesPerPack(i)
    }

    fun updatePackPrice(i: Float){
        preferencesRepo.updatePackPrice(i)
    }

    fun updateCurrency(symbol: String){
        preferencesRepo.updateCurrency(symbol)
    }
}