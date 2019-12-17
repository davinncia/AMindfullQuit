package com.example.amindfullquit.smoking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList

class SmokingDataViewModel : ViewModel() {

    private val consumption = MutableLiveData<List<SmokingData>>()

    //These will be get from preferences
    private val defaultConsumption = 10
    private val startingTime = 1254738759279L


    init {
        fetchSmokingData()
    }

    private fun fetchSmokingData(){
        //Save starting day on database creation
        val data = ArrayList<SmokingData>()
        data.add(SmokingData(987687597086087, 6))
        data.add(SmokingData(987687597086087, 10))
        data.add(SmokingData(987687597086087, 0))
        data.add(SmokingData(987687597086087, 9))
        data.add(SmokingData(987687597086087, 5))

        //incorporate default day by adding a day in a for loop (?)

        consumption.value = data
    }

    fun getConsumption(): LiveData<List<SmokingData>>{
        return consumption
    }
}