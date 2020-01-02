package com.example.amindfullquit.smoking

import androidx.lifecycle.*

class SmokingDataViewModel : ViewModel() {

    private val chartItemsUi = MediatorLiveData<List<SmokingChartItemUi>>()
    lateinit var cigarettesAvoided: MutableLiveData<Int> //To liveData

    private val userDataLiveData = MutableLiveData<List<SmokingData>>()

    private val maxHeightLiveData = MutableLiveData<Int>()

    //These will be get from preferences
    private val defaultConsumption = 6
    private val startingTime = 1576163060185L


    init {
        fetchSmokingData()

        //Waiting for both View Height & Db data to be obtain before mapping

        chartItemsUi.addSource(userDataLiveData, Observer {
            if (maxHeightLiveData.value != null){
                mapSmokingDataForView(ArrayList(it), maxHeightLiveData.value!!)
            }
        })

        chartItemsUi.addSource(maxHeightLiveData) {
            if (userDataLiveData.value != null){
                mapSmokingDataForView(ArrayList(userDataLiveData.value!!), it)
            }
        }

        cigarettesAvoided = MutableLiveData(6)//Transformations.map(database)
    }

    private fun fetchSmokingData(){
        //Save starting day on database creation

        //Getting SmokingData db
        //60_000 * 60 * 24 = 86_400_000 = One day
        val dbList = arrayListOf( //DEBUG
            SmokingData(startingTime + 86_400_000, 10), //2nd day
            SmokingData(startingTime + 86_400_000 * 3, 10) //4th day
        )

        userDataLiveData.value = dbList
    }

    private fun mapSmokingDataForView(dbList: ArrayList<SmokingData>, maxHeight: Int){
        val dataUi = ArrayList<SmokingChartItemUi>()
        val todayMilli = System.currentTimeMillis()
        val dayNbr = ((todayMilli - startingTime) / 86_400_000).toInt()

        val maxConsumption = getMaxConsumption(dbList)

        for (i in 0..dayNbr) {

            if (dbList.size == 0 || ((dbList[0].creationTimeStamp - startingTime) / 86_400_000).toInt() != i) {
                //Day not in data base
                val height = (maxHeight / maxConsumption * defaultConsumption)
                dataUi.add(SmokingChartItemUi((dayNbr * 86_400_000L).toString(), defaultConsumption, height))
            } else {
                //Day in database
                val height = (maxHeight / maxConsumption * dbList[0].cigaretteNbr)
                dataUi.add(SmokingChartItemUi(dbList[0].creationTimeStamp.toString(), dbList[0].cigaretteNbr, height))
                dbList.removeAt(0)
            }

        }
        chartItemsUi.value = dataUi
    }


    private fun getMaxConsumption(dataList: List<SmokingData>): Int{
        var max = defaultConsumption
        for (data in dataList){
            if (data.cigaretteNbr > max)
                max = data.cigaretteNbr
        }
        return max
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