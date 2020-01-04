package com.example.amindfullquit.smoking

import android.util.Log
import androidx.lifecycle.*
import com.example.amindfullquit.repository.PreferencesRepository

class SmokingDataViewModel(preferencesRepo: PreferencesRepository) : ViewModel() {

    private val chartItemsUi = MediatorLiveData<List<SmokingChartItemUi>>()

    private val cigarettesAvoided = MediatorLiveData<Int>()
    val cigarettesAvoidedLiveData: LiveData<Int> = cigarettesAvoided

    private val userDataLiveData = MutableLiveData<List<SmokingData>>()

    private val maxHeightLiveData = MutableLiveData<Int>()

    private val defaultConsumptionLiveData = preferencesRepo.defaultConsumptionLiveData()
    private val startingTimeLiveData = preferencesRepo.startingTimeLiveData()
    private val previousConsumptionLiveData = preferencesRepo.previousConsumptionLiveData()


    init {
        chartItemsUi.addSource(startingTimeLiveData) {
            fetchSmokingData(it)
        }

        //CHART ITEMS
        //View Height, Db data & default consumption as sources. Trigger only when all variables are set
        chartItemsUi.addSource(defaultConsumptionLiveData) {
            if (maxHeightLiveData.value != null && userDataLiveData.value != null){
                mapSmokingDataForView(ArrayList(userDataLiveData.value!!), maxHeightLiveData.value!!,
                    startingTimeLiveData.value!!, it)
            }
        }

        chartItemsUi.addSource(userDataLiveData) {
            if (maxHeightLiveData.value != null && defaultConsumptionLiveData.value != null){
                mapSmokingDataForView(ArrayList(it), maxHeightLiveData.value!!,
                    startingTimeLiveData.value!!, defaultConsumptionLiveData.value!!)
            }
        }

        chartItemsUi.addSource(maxHeightLiveData) {
            if (userDataLiveData.value != null && defaultConsumptionLiveData.value != null){
                mapSmokingDataForView(ArrayList(userDataLiveData.value!!), it,
                    startingTimeLiveData.value!!, defaultConsumptionLiveData.value!!)
            }
        }

        //CIGARETTE AVOIDED
        cigarettesAvoided.addSource(previousConsumptionLiveData) {
            if (userDataLiveData.value != null) {
                calculateCigarettesAvoidedNbr(
                    userDataLiveData.value!!, calculateDaysNbr(),
                    it, defaultConsumptionLiveData.value!!
                )
            }
        }

    }

    private fun fetchSmokingData(startingTime: Long){
        //Save starting day on database creation

        //Getting SmokingData db
        //60_000 * 60 * 24 = 86_400_000 = One day
        val dbList = arrayListOf( //DEBUG
            SmokingData(startingTime + 86_400_000, 10), //2nd day
            SmokingData(startingTime + 86_400_000 * 3, 10) //4th day
        )

        userDataLiveData.value = dbList
    }

    //Chart items construction from data
    private fun mapSmokingDataForView(dbList: ArrayList<SmokingData>, maxHeight: Int,
                                      startingTime: Long, defaultConsumption: Int){

        Log.d("debuglog", "Mapped")
        val dataUi = ArrayList<SmokingChartItemUi>()
        val dayNbr = calculateDaysNbr()

        val maxConsumption = getMaxConsumption(dbList, defaultConsumption)

        for (i in 0..dayNbr) {

            if (dbList.size == 0 || ((dbList[0].creationTimeStamp - startingTime) / 86_400_000).toInt() != i) {
                //Day not in data base
                var height = (maxHeight / maxConsumption * defaultConsumption)
                if (height == 0) height = 25 //So it's still visible in chart
                dataUi.add(SmokingChartItemUi((dayNbr * 86_400_000L).toString(), defaultConsumption, height))
            } else {
                //Day in database
                var height = (maxHeight / maxConsumption * dbList[0].cigaretteNbr)
                if (height == 0) height = 25
                dataUi.add(SmokingChartItemUi(dbList[0].creationTimeStamp.toString(), dbList[0].cigaretteNbr, height))
                dbList.removeAt(0)
            }

        }
        chartItemsUi.value = dataUi

        calculateCigarettesAvoidedNbr(dbList, dayNbr, previousConsumptionLiveData.value, defaultConsumption)
    }

    private fun calculateDaysNbr(): Int{
        val todayMilli = System.currentTimeMillis()
        return ((todayMilli - startingTimeLiveData.value!!) / 86_400_000).toInt()
    }

    private fun getMaxConsumption(dataList: List<SmokingData>, default: Int): Int{
        var max = default
        for (data in dataList){
            if (data.cigaretteNbr > max)
                max = data.cigaretteNbr
        }
        return max
    }

    private fun calculateCigarettesAvoidedNbr(dbList: List<SmokingData>, days: Int,
                                              previousConsumption: Int?, defaultConsumption: Int){
        previousConsumption?: return

        Log.d("debuglog", "AVOIDED")
        val oldQuantity = days * previousConsumption

        var actualQuantity = 0
        for (item in dbList){
            actualQuantity += item.cigaretteNbr
        }
        actualQuantity += (days - dbList.size) * defaultConsumption

        cigarettesAvoided.value = oldQuantity - actualQuantity
    }

    fun setMaxBarHeight(viewHeight: Int){
        maxHeightLiveData.value = viewHeight - 16
    }

    fun getSmokingChartItems(): LiveData<List<SmokingChartItemUi>>{
        return chartItemsUi
    }

    fun getChartDetails(i: Int): String {
        return if (chartItemsUi.value == null) ""
        else chartItemsUi.value!![i].cigaretteNbr.toString()
    }
}