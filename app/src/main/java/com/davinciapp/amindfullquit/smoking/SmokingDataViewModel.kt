package com.davinciapp.amindfullquit.smoking

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.davinciapp.amindfullquit.meditation.log_fragment.LogDataUi
import com.davinciapp.amindfullquit.repository.PreferencesRepository
import com.davinciapp.amindfullquit.repository.SmokingDataRepository
import com.davinciapp.amindfullquit.utils.millisInDay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class SmokingDataViewModel(private val preferencesRepo: PreferencesRepository,
                           private val smokingDataRepo: SmokingDataRepository) : ViewModel() {

    private val dbDataLiveData = smokingDataRepo.allData
    private val maxViewHeightLiveData = MutableLiveData<Int>()

    //Rmq: A mediator needs to be observed in unit test otherwise won't be updated #Schrodinger's cat
    @VisibleForTesting
    val chartItemsUiMediator = MediatorLiveData<List<SmokingChartItemUi>>()
    @VisibleForTesting
    val roundDataItemUi = MediatorLiveData<List<LogDataUi>>()


    private val maxQuantityMutable = MutableLiveData<Int>()
    val maxQuantityLiveData: LiveData<Int> = maxQuantityMutable


    //PREFERENCES
    private val defaultConsumptionLiveData = preferencesRepo.defaultConsumptionLiveData()
    private val startingTimeLiveData = preferencesRepo.startingTimeLiveData()
    private val previousConsumptionLiveData = preferencesRepo.previousConsumptionLiveData()
    private val cigarettesPerPackLiveData = preferencesRepo.cigarettesPerPackLiveData()
    private val packPriceLiveData = preferencesRepo.packPriceLiveData()

    //EMPTY GRAPH VIEW
    val emptyGraph = MutableLiveData<Boolean>()


    init {

        //CHART ITEMS TRIGGERING
        //View Height & Db data as sources. Trigger only when all variables are set
        chartItemsUiMediator.addSource(dbDataLiveData) {
            if (enoughDataToProceed()) {
                mapChartItems(
                    ArrayList(it), maxViewHeightLiveData.value!!,
                    startingTimeLiveData.value!!, defaultConsumptionLiveData.value!!
                )
            }
        }

        chartItemsUiMediator.addSource(maxViewHeightLiveData) {
            if (enoughDataToProceed()) {
                mapChartItems(
                    ArrayList(dbDataLiveData.value!!), it,
                    startingTimeLiveData.value!!, defaultConsumptionLiveData.value!!
                )
            }
        }

        //Preferences as source
        chartItemsUiMediator.addSource(preferencesRepo.prefHasChanged) {
            if (!enoughDataToProceed()) return@addSource

            mapChartItems(
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
    private fun mapChartItems(
        dbList: ArrayList<SmokingData>, maxHeight: Int,
        startingTime: Long, defaultConsumption: Int
    ) {

        //Log.d("debuglog", dbList.size.toString())
        //Log.d("debuglog", "MAPPING")

        val maxConsumption = getMaxConsumption(dbList, defaultConsumption)
        maxQuantityMutable.value = maxConsumption //Used for bar height

        //Initial list with only default items
        var height = (maxHeight / maxConsumption) * defaultConsumption
        if (height == 0) height = 25 //So it's still visible in chart
        val dayNbr = calculateDaysNbr()

        val dataUi = ArrayList(List(dayNbr + 1) {
            SmokingChartItemUi(
                startingTime + it * millisInDay,
                formatDateString(startingTime + it * millisInDay),
                defaultConsumption,
                height)
        })

        //Filling list with data from db
        for (item in dbList) {
            val day = ((item.creationTimeStamp - startingTime) / millisInDay).toInt()

            //HEIGHT
            height = (maxHeight / maxConsumption * item.cigaretteNbr)
            if (height == 0) height = 25

            //DESCRIPTION
            val dateStr = formatDateString(item.creationTimeStamp)

            if (day > 0) {
                dataUi[day] = SmokingChartItemUi(
                    item.creationTimeStamp, dateStr, item.cigaretteNbr, height
                )
            }
        }

        chartItemsUiMediator.value = dataUi

        //Check for empty view
        emptyGraph.value = dataUi.isEmpty()

        mapRoundDataItems(dbDataLiveData.value!!, dayNbr)
    }


    private fun calculateDaysNbr(): Int {
        val todayMilli = System.currentTimeMillis()
        return ((todayMilli - startingTimeLiveData.value!!) / millisInDay).toInt()
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
        val items = ArrayList<LogDataUi>()

        //CIGARETTES AVOIDED
        val oldQuantity = days * previousConsumptionLiveData.value!!
        var actualQuantity = 0
        for (item in dbList) {
            actualQuantity += item.cigaretteNbr
        }
        actualQuantity += (days - dbList.size) * defaultConsumptionLiveData.value!!

        val cigarettesAvoided = oldQuantity - actualQuantity

        items.add(LogDataUi("Mindfuled", String.format("%,d", cigarettesAvoided)))

        //MONEY SAVED
        if (cigarettesPerPackLiveData.value!! > 0) {
            val cigarettePrice: Double =
                packPriceLiveData.value!!.toDouble() / cigarettesPerPackLiveData.value!!
            val currency = preferencesRepo.currencyLiveData().value

            val money = cigarettesAvoided * cigarettePrice
            val economies =
                if (money < 100) String.format("%.2f", money)
                else String.format("%,d", money.roundToInt())
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
        return chartItemsUiMediator
    }

    fun getLogDataItems(): LiveData<List<LogDataUi>> {
        return roundDataItemUi
    }

}