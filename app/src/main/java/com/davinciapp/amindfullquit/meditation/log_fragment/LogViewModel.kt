package com.davinciapp.amindfullquit.meditation.log_fragment

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.davinciapp.amindfullquit.meditation.MeditationSession
import com.davinciapp.amindfullquit.repository.MeditationSessionRepository
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LogViewModel(application: Application, meditationRepo: MeditationSessionRepository)
    : AndroidViewModel(application) {

    private val sessionsLiveData = meditationRepo.allSessions

    val emptyGraphLiveData = MutableLiveData<Boolean>()

    private val maxSessionTime = MutableLiveData<Int>()
    val maxSessionLiveData = maxSessionTime

    private val chartItemsLiveData = MediatorLiveData<List<ChartItemUi>>()
    val logDataLiveData = Transformations.map(sessionsLiveData) {
        mapLogData(it)
    }

    private val maxHeightLiveData = MutableLiveData<Int>()

    init {
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

    //-------------------------------------------------------------------------------------------//
    //                                  R O U N D   D A T A
    //-------------------------------------------------------------------------------------------//
    private fun mapLogData(sessions: List<MeditationSession>): List<LogDataUi>{
        val data = ArrayList<LogDataUi>()

        //TOTAL TIME
        var totalTime = 0
        var valueStr: String
        val unit: String

        for (i in sessions){
            totalTime += i.minutes
        }
        valueStr = if (totalTime > 60) {
            unit = "hours"
            val hours = totalTime / 60
            String.format("%,d", hours)
        } else {
            unit = "min"
            "$totalTime"
        }

        data.add(LogDataUi("Total ($unit)", valueStr))

        //MAX TIME
        data.add(LogDataUi("Max", getLongestSessionTime(sessions).toString()))

        //AVERAGE TIME
        if (sessions.isNotEmpty()) {
            val averageTime = totalTime / sessions.size
            data.add(LogDataUi("Average", "$averageTime"))
        }

        return data
    }

    //-------------------------------------------------------------------------------------------//
    //                                        C H A R T
    //-------------------------------------------------------------------------------------------//
    private fun mapChartItems(sessions: List<MeditationSession>, maxHeight: Int){
        //TODO: starting date, empty days

        emptyGraphLiveData.value = sessions.isEmpty()

        val chartItems = ArrayList<ChartItemUi>()

        val maxTime = getLongestSessionTime(sessions)

        for (i in sessions) {
            //VIEW HEIGHT
            val height = (maxHeight / maxTime * i.minutes)
            //DATE
            val simpleFormat = SimpleDateFormat("EEE  dd  MMM", Locale.getDefault())
            val date = simpleFormat.format(i.creationTimeStamp)

            chartItems.add(ChartItemUi(date, i.minutes, height))
        }

        chartItemsLiveData.value = chartItems
    }

    //-------------------------------------------------------------------------------------------//
    //                                      H E L P E R S
    //-------------------------------------------------------------------------------------------//
    private fun getLongestSessionTime(sessions: List<MeditationSession>): Int{

        var maxTime = 0
        for (i in sessions){
            if (i.minutes > maxTime){
                maxTime = i.minutes
            }
        }
        maxSessionTime.value = maxTime

        return maxTime
    }

    fun getChartItems(): LiveData<List<ChartItemUi>>{
        return chartItemsLiveData
    }

    fun setMaxBarHeight(viewHeight: Int){
        maxHeightLiveData.value = viewHeight - 16
    }
}