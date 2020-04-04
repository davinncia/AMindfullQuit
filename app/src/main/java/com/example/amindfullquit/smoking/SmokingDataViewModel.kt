package com.example.amindfullquit.smoking

import android.util.Log
import androidx.lifecycle.*
import com.example.amindfullquit.meditation.log_fragment.LogDataUi
import com.example.amindfullquit.repository.PreferencesRepository
import com.example.amindfullquit.repository.SmokingDataRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SmokingDataViewModel(private val preferencesRepo: PreferencesRepository,
                           private val smokingDataRepo: SmokingDataRepository) : ViewModel() {

    //TODO: empty / starting view
    private val dbDataLiveData = smokingDataRepo.allData
    //private val userDataLiveData = MutableLiveData<List<SmokingData>>()
    private val maxViewHeightLiveData = MutableLiveData<Int>()

    private val chartItemsUi = MediatorLiveData<List<SmokingChartItemUi>>()
    private val roundDataItemUi = MediatorLiveData<List<LogDataUi>>()

    val cigarettesAvoidedLiveData: LiveData<List<LogDataUi>> = roundDataItemUi
    private val maxQuantityMutable = MutableLiveData<Int>()
    val maxQuantityLiveData: LiveData<Int> = maxQuantityMutable


    //PREFERENCES
    private val defaultConsumptionLiveData = preferencesRepo.defaultConsumptionLiveData()
    private val startingTimeLiveData = preferencesRepo.startingTimeLiveData()
    private val previousConsumptionLiveData = preferencesRepo.previousConsumptionLiveData()
    private val cigarettesPerPackLiveData = preferencesRepo.cigarettesPerPackLiveData()
    private val packPriceLiveData = preferencesRepo.packPriceLiveData()


    init {
        //userDataLiveData = smokingDataRepo.allData

        //CHART ITEMS TRIGGERING
        //View Height & Db data as sources. Trigger only when all variables are set
        chartItemsUi.addSource(dbDataLiveData) {
            if (enoughDataToProceed()) {
                mapSmokingChartItems(
                    ArrayList(it), maxViewHeightLiveData.value!!,
                    startingTimeLiveData.value!!, defaultConsumptionLiveData.value!!
                )
            }
        }

        chartItemsUi.addSource(maxViewHeightLiveData) {
            if (enoughDataToProceed()) {
                mapSmokingChartItems(
                    ArrayList(dbDataLiveData.value!!), it,
                    startingTimeLiveData.value!!, defaultConsumptionLiveData.value!!
                )
            }
        }

        //Preferences as source
        chartItemsUi.addSource(preferencesRepo.prefHasChanged) {
            if (!enoughDataToProceed()) return@addSource

            mapSmokingChartItems(
                ArrayList(dbDataLiveData.value!!), maxViewHeightLiveData.value!!,
                startingTimeLiveData.value!!, defaultConsumptionLiveData.value!!
            )
        }

    }

    private fun enoughDataToProceed() = (dbDataLiveData.value != null && maxViewHeightLiveData.value != null)


    //-------------------------------------------------------------------------------------------//
    //                                     D A T A   B A S E
    //-------------------------------------------------------------------------------------------//
    fun updateSmokingData(timeStamp: Long, quantity: Int) {
        viewModelScope.launch {
            //Check if exist in db -> update
            val daysStored = dbDataLiveData.value!!.map { it.creationTimeStamp }
            if (daysStored.contains(timeStamp)) {
                smokingDataRepo.update(SmokingData(timeStamp, quantity))
            } else {
                //Not stored -> insert
                smokingDataRepo.insert(SmokingData(timeStamp, quantity))
            }
        }
    }
    //-------------------------------------------------------------------------------------------//
    //                                         C H A R T
    //-------------------------------------------------------------------------------------------//
    //Chart items construction from data
    //TODO: find a solution better than mapping everything on modif ?
    private fun mapSmokingChartItems(
        dbList: ArrayList<SmokingData>, maxHeight: Int,
        startingTime: Long, defaultConsumption: Int
    ) {
        val dataUi = ArrayList<SmokingChartItemUi>()
        val dayNbr = calculateDaysNbr()

        val maxConsumption = getMaxConsumption(dbList, defaultConsumption)
        maxQuantityMutable.value = maxConsumption //Used for bar height

        for (i in 0..dayNbr) {

            //TODO: something wrong with timeStamp... One precise is enough
            if (dbList.size == 0 || ((dbList[0].creationTimeStamp - startingTime) / 86_400_000).toInt() != i) {
                //Day is not in data base -> create default smoking data
                var height = (maxHeight / maxConsumption) * defaultConsumption
                if (height == 0) height = 25 //So it's still visible in chart
                dataUi.add(
                    SmokingChartItemUi(
                        startingTime + i * 86_400_000,
                        formatDateString(startingTime + i * 86_400_000),
                        defaultConsumption,
                        height
                    )
                )
            } else {
                //Day is in database
                //HEIGHT
                var height = (maxHeight / maxConsumption * dbList[0].cigaretteNbr)
                if (height == 0) height = 25

                //DESCRIPTION
                val description = formatDateString(dbList[0].creationTimeStamp)

                dataUi.add(SmokingChartItemUi(
                    dbList[0].creationTimeStamp, description, dbList[0].cigaretteNbr, height)
                )
                dbList.removeAt(0)
            }

        }
        chartItemsUi.value = dataUi

        mapRoundDataItems(dbDataLiveData.value!!, dayNbr)
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
        return if (max == 0) 1 else max
    }

    //-------------------------------------------------------------------------------------------//
    //                                  R O U N D   D A T A
    //-------------------------------------------------------------------------------------------//
    private fun mapRoundDataItems(dbList: List<SmokingData>, days: Int) {
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
        maxViewHeightLiveData.value = viewHeight - 16
    }

    fun getSmokingChartItems(): LiveData<List<SmokingChartItemUi>> {
        return chartItemsUi
    }

}