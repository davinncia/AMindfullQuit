package com.example.amindfullquit.smoking

import android.util.Log
import androidx.lifecycle.*
import com.example.amindfullquit.meditation.log_fragment.LogDataUi
import com.example.amindfullquit.repository.PreferencesRepository
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class SmokingDataViewModel(val preferencesRepo: PreferencesRepository) : ViewModel() {

    private val chartItemsUi = MediatorLiveData<List<SmokingChartItemUi>>()

    private val roundDataItemUi = MediatorLiveData<List<LogDataUi>>()
    val cigarettesAvoidedLiveData: LiveData<List<LogDataUi>> = roundDataItemUi

    private val maxQuantityMutable = MutableLiveData<Int>()
    val maxQuantityLiveData: LiveData<Int> = maxQuantityMutable

    private val userDataLiveData = MutableLiveData<List<SmokingData>>()

    private val maxHeightLiveData = MutableLiveData<Int>()

    //PREFERENCES
    //TODO Theses will be values
    private val defaultConsumptionLiveData = preferencesRepo.defaultConsumptionLiveData()
    private val startingTimeLiveData = preferencesRepo.startingTimeLiveData()
    private val previousConsumptionLiveData = preferencesRepo.previousConsumptionLiveData()
    private val cigarettesPerPackLiveData = preferencesRepo.cigarettesPerPackLiveData()
    private val packPriceLiveData = preferencesRepo.packPriceLiveData()


    init {
        chartItemsUi.addSource(startingTimeLiveData) {
            fetchSmokingData(it)
        }

        //CHART ITEMS TRIGGERING
        //View Height & Db data as sources. Trigger only when all variables are set
        chartItemsUi.addSource(userDataLiveData) {
            if (maxHeightLiveData.value != null && defaultConsumptionLiveData.value != null) {
                mapSmokingChartItems(
                    ArrayList(it), maxHeightLiveData.value!!,
                    startingTimeLiveData.value!!, defaultConsumptionLiveData.value!!
                )
            }
        }

        chartItemsUi.addSource(maxHeightLiveData) {
            if (userDataLiveData.value != null && defaultConsumptionLiveData.value != null) {
                mapSmokingChartItems(
                    ArrayList(userDataLiveData.value!!), it,
                    startingTimeLiveData.value!!, defaultConsumptionLiveData.value!!
                )
            }
        }

        chartItemsUi.addSource(preferencesRepo.prefHasChanged) {
            if (userDataLiveData.value == null) return@addSource

            mapSmokingChartItems(
                ArrayList(userDataLiveData.value!!), maxHeightLiveData.value!!,
                startingTimeLiveData.value!!, defaultConsumptionLiveData.value!!
            )
        }

    }

    //-------------------------------------------------------------------------------------------//
    //                                     D A T A   B A S E
    //-------------------------------------------------------------------------------------------//
    private fun fetchSmokingData(startingTime: Long) {
        //Save starting day on database creation

        //Getting SmokingData db
        //60_000 * 60 * 24 = 86_400_000 = One day
        val dbList = arrayListOf( //DEBUG
            SmokingData(startingTime + 86_400_000, 10), //2nd day
            SmokingData(startingTime + 86_400_000 * 3, 10) //4th day
        )

        userDataLiveData.value = dbList
    }

    //-------------------------------------------------------------------------------------------//
    //                                         C H A R T
    //-------------------------------------------------------------------------------------------//
    //Chart items construction from data
    private fun mapSmokingChartItems(
        dbList: ArrayList<SmokingData>, maxHeight: Int,
        startingTime: Long, defaultConsumption: Int
    ) {

        Log.d("debuglog", "Mapped")
        val dataUi = ArrayList<SmokingChartItemUi>()
        val dayNbr = calculateDaysNbr()

        val maxConsumption = getMaxConsumption(dbList, defaultConsumption)
        maxQuantityMutable.value = maxConsumption //Used for bar height

        for (i in 0..dayNbr) {

            if (dbList.size == 0 || ((dbList[0].creationTimeStamp - startingTime) / 86_400_000).toInt() != i) {
                //Day not in data base
                var height = (maxHeight / maxConsumption * defaultConsumption)
                if (height == 0) height = 25 //So it's still visible in chart
                dataUi.add(
                    SmokingChartItemUi(
                        (dayNbr * 86_400_000L).toString(),
                        defaultConsumption,
                        height
                    )
                )
            } else {
                //Day in database
                //HEIGHT
                var height = (maxHeight / maxConsumption * dbList[0].cigaretteNbr)
                if (height == 0) height = 25

                //DESCRIPTION
                val description = formatDateString(dbList[0].creationTimeStamp)

                dataUi.add(SmokingChartItemUi(description, dbList[0].cigaretteNbr, height))
                dbList.removeAt(0)
            }

        }
        chartItemsUi.value = dataUi

        mapRoundDataItems(userDataLiveData.value!!, dayNbr)
    }

    private fun calculateDaysNbr(): Int {
        val todayMilli = System.currentTimeMillis()
        return ((todayMilli - startingTimeLiveData.value!!) / 86_400_000).toInt()
    }

    private fun formatDateString(timeStamp: Long): String {

        val dateFormat = SimpleDateFormat("EEE dd MMM", Locale.getDefault())
        return dateFormat.format(timeStamp)
    }

    private fun getMaxConsumption(dataList: List<SmokingData>, default: Int): Int {
        var max = default
        for (data in dataList) {
            if (data.cigaretteNbr > max)
                max = data.cigaretteNbr
        }
        return max
    }

    //-------------------------------------------------------------------------------------------//
    //                                  R O U N D   D A T A
    //-------------------------------------------------------------------------------------------//
    private fun mapRoundDataItems(dbList: List<SmokingData>, days: Int) {

        //TODO: launches 3 times.... Make a generic event live data "preferencesChanged"
        //Because it's getting way to complicated

        Log.d("debuglog", "MAPPING LOG")
        val items = ArrayList<LogDataUi>()

        //CIGARETTES AVOIDED
        val oldQuantity = days * previousConsumptionLiveData.value!!
        var actualQuantity = 0
        for (item in dbList) {
            actualQuantity += item.cigaretteNbr
        }
        actualQuantity += (days - dbList.size) * defaultConsumptionLiveData.value!!

        val cigarettesAvoided = oldQuantity - actualQuantity

        items.add(LogDataUi("Mindfullded", cigarettesAvoided.toString()))

        //MONEY SAVED
        if (cigarettesPerPackLiveData.value!! > 0) {
            val cigarettePrice: Double =
                packPriceLiveData.value!!.toDouble() / cigarettesPerPackLiveData.value!!
            val currency = preferencesRepo.currencyLiveData().value
            val economies = String.format("%.2f", cigarettesAvoided * cigarettePrice)
            items.add(LogDataUi("Saved !", economies + currency))
        }

        //AVERAGE PER DAY
        var totalCount = 0
        for (i in dbList) {
            totalCount += i.cigaretteNbr
        }
        totalCount += (days - dbList.size) * defaultConsumptionLiveData.value!!
        val average = String.format("%.1f", totalCount.toDouble() / days)
        items.add(LogDataUi("Per day", average))

        roundDataItemUi.value = items
    }

    //-------------------------------------------------------------------------------------------//
    //                                 G E T T E R S  /  S E T T E R S
    //-------------------------------------------------------------------------------------------//
    fun setMaxBarHeight(viewHeight: Int) {
        maxHeightLiveData.value = viewHeight - 16
    }

    fun getSmokingChartItems(): LiveData<List<SmokingChartItemUi>> {
        return chartItemsUi
    }

}