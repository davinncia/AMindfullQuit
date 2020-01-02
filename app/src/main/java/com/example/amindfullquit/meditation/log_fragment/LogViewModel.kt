package com.example.amindfullquit.meditation.log_fragment

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.amindfullquit.meditation.MeditationSession
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class LogViewModel : ViewModel() {

    private val sessionsLiveData = MutableLiveData<List<MeditationSession>>()

    private val chartItemsLiveData = MediatorLiveData<List<ChartItemUi>>()
    val logDataLiveData = Transformations.map(sessionsLiveData) {
        mapLogData(it)
    }

    private val maxHeightLiveData = MutableLiveData<Int>()

    init {
        //Fetch from database
        loadSessions()

        //Making sure both view height & sessions are available to map

        chartItemsLiveData.addSource(sessionsLiveData, Observer {
            if (maxHeightLiveData.value == null) return@Observer //Wait max height to be set
            else mapChartItems(it, maxHeightLiveData.value!!)
        })

        chartItemsLiveData.addSource(maxHeightLiveData, Observer {
            if (sessionsLiveData.value == null) return@Observer //Wait for sessions to load
            else mapChartItems(sessionsLiveData.value!!, it)
        })
    }

    fun getChartItems(): LiveData<List<ChartItemUi>>{
        return chartItemsLiveData
    }

    private fun loadSessions(){

        val sessions = arrayListOf(
            MeditationSession(17268276, 10),
            MeditationSession(17268276, 12),
            MeditationSession(17268276, 6),
            MeditationSession(17268276, 11),
            MeditationSession(17268276, 10),
            MeditationSession(17268276, 12),
            MeditationSession(17268276, 6),
            MeditationSession(17268276, 11)
        )
        sessionsLiveData.value = sessions
    }

    private fun mapLogData(sessions: List<MeditationSession>): List<LogDataUi>{
        val data = ArrayList<LogDataUi>()

        //TOTAL TIME
        var totalTime = 0
        for (i in sessions){
            totalTime += i.minutes
        }
        data.add(LogDataUi("Total min", totalTime))

        //MAX TIME
        data.add(LogDataUi("Max time", getLongestSessionTime(sessions)))

        //AVERAGE TIME
        val averageTime = totalTime / sessions.size
        data.add(LogDataUi("Average time", averageTime))

        return data
    }

    private fun mapChartItems(sessions: List<MeditationSession>, maxHeight: Int){

        val chartItems = ArrayList<ChartItemUi>()

        val maxTime = getLongestSessionTime(sessions)

        for (i in sessions) {
            //VIEW HEIGHT
            val height = (maxHeight / maxTime * i.minutes)
            //DESCRIPTION
            val simpleFormat = SimpleDateFormat("EEE  FF  MMM", Locale.getDefault())
            val date = simpleFormat.format(i.creationTimeStamp)
            val description = date + "\n" + i.minutes + "min"

            chartItems.add(ChartItemUi(description, i.minutes, height))
        }
        chartItemsLiveData.value = chartItems
    }

    fun setMaxBarHeight(viewHeight: Int){
        maxHeightLiveData.value = viewHeight - 16
    }

    //HELPERS
    private fun getLongestSessionTime(sessions: List<MeditationSession>): Int{

        var maxTime = 0
        for (i in sessions){
            if (i.minutes > maxTime){
                maxTime = i.minutes
            }
        }
        return maxTime
    }
}