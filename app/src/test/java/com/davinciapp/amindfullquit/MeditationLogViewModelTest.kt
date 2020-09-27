package com.davinciapp.amindfullquit

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.davinciapp.amindfullquit.meditation.MeditationSession
import com.davinciapp.amindfullquit.meditation.log_fragment.ChartItemUi
import com.davinciapp.amindfullquit.meditation.log_fragment.LogViewModel
import com.davinciapp.amindfullquit.repository.MeditationSessionRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeoutException

@RunWith(MockitoJUnitRunner::class)
class MeditationLogViewModelTest {

    //Run tasks synchronously
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application
    @Mock
    private lateinit var meditationSessionRepo: MeditationSessionRepository

    private lateinit var viewModel: LogViewModel

    @Before
    fun setUp() {
        Mockito.`when`(meditationSessionRepo.allSessions).thenReturn(
            MutableLiveData(dummySessions)
        )
        viewModel = LogViewModel(application, meditationSessionRepo)

        viewModel.chartItemsMediator.observeForever{} //Allow it to update
    }

    @Test(expected = TimeoutException::class)
    fun when_ViewHeightIsNotGiven_Then_ItemsAreNotMapped() {
        (viewModel.getChartItems()).getOrAwaitValue()
    }

    //-------------------------------------------------------------------------------------------//
    //                                  L O G    D A T A
    //-------------------------------------------------------------------------------------------//
    @Test
    fun threeLogDataAreCommunicated() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val logData = viewModel.logDataLiveData.getOrAwaitValue()
        //THEN
        Assert.assertEquals(3, logData.size)
    }

    @Test
    fun maxSessionTimeCorrectlyComputed() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val maxTime: Int? = viewModel.maxSessionLiveData.getOrAwaitValue()
        //THEN
        Assert.assertEquals(2, maxTime)
    }

    @Test
    fun totalSessionTimeCorrectlyComputed() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val logData = viewModel.logDataLiveData.getOrAwaitValue()
        //THEN
        Assert.assertEquals("Total (min)", logData[0].description)
        Assert.assertEquals("3", logData[0].value)
    }

    @Test
    fun averageTimeCorrectlyComputed() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val logData = viewModel.logDataLiveData.getOrAwaitValue()
        //THEN
        Assert.assertEquals("Average", logData[2].description)
        Assert.assertEquals("1.5", logData[2].value)
    }

    //-------------------------------------------------------------------------------------------//
    //                                  C H A R T   I T E M S
    //-------------------------------------------------------------------------------------------//
    @Test
    fun chartItemsAreCorrectlyMapped() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val items = (viewModel.getChartItems()).getOrAwaitValue()
        val expectedItems = listOf(
            ChartItemUi("jeu.  01  janv.", 1, 42),
            ChartItemUi("sam.  02  juin", 2, 84)
        )
        //THEN
        Assert.assertEquals(2, items.size)
        Assert.assertEquals(expectedItems[0], items[0])
        Assert.assertEquals(expectedItems[1], items[1])

    }

    //-------------------------------------------------------------------------------------------//
    //                                  D U M M Y    D A T A
    //-------------------------------------------------------------------------------------------//
    private val dummySessions = listOf<MeditationSession>(
        MeditationSession(11111111, 1),
        MeditationSession(2222222222222, 2)
    )
}