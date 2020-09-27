package com.davinciapp.amindfullquit

import android.app.Application
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.davinciapp.amindfullquit.meditation.log_fragment.ChartItemUi
import com.davinciapp.amindfullquit.repository.PreferencesRepository
import com.davinciapp.amindfullquit.repository.SmokingDataRepository
import com.davinciapp.amindfullquit.smoking.SmokingChartItemUi
import com.davinciapp.amindfullquit.smoking.SmokingData
import com.davinciapp.amindfullquit.smoking.SmokingDataViewModel
import com.davinciapp.amindfullquit.utils.millisInDay
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
class SmokingDataViewModelTest {

    //Run tasks synchronously
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application
    @Mock
    private lateinit var smokingDataRepo: SmokingDataRepository
    @Mock
    private lateinit var prefRepo: PreferencesRepository

    private lateinit var viewModel: SmokingDataViewModel

    //User info
    private val startingTime = System.currentTimeMillis() - millisInDay * 7 // 7 days ago
    private val defaultCigNbr = 0

    @Before
    fun setUp() {
        Mockito.`when`(smokingDataRepo.allData).thenReturn(MutableLiveData(dummyData))
        Mockito.`when`(prefRepo.prefHasChanged).thenReturn(MutableLiveData(false))
        Mockito.`when`(prefRepo.startingTimeLiveData()).thenReturn(MutableLiveData(startingTime))
        Mockito.`when`(prefRepo.defaultConsumptionLiveData()).thenReturn(MutableLiveData(defaultCigNbr))
        Mockito.`when`(prefRepo.cigarettesPerPackLiveData()).thenReturn(MutableLiveData(10))
        Mockito.`when`(prefRepo.currencyLiveData()).thenReturn(MutableLiveData("€"))
        Mockito.`when`(prefRepo.packPriceLiveData()).thenReturn(MutableLiveData(10F))
        Mockito.`when`(prefRepo.previousConsumptionLiveData()).thenReturn(MutableLiveData(10))

        viewModel = SmokingDataViewModel(prefRepo, smokingDataRepo)
        viewModel.chartItemsUiMediator.observeForever{}
    }

    //Always data to show...
    //@Test
    //fun when_NoDataToShow_Then_EmptyViewEvent() {
    //    //GIVEN no data
    //    Mockito.`when`(smokingDataRepo.allData).thenReturn(MutableLiveData(listOf()))
    //    viewModel.setMaxBarHeight(100)
    //    //WHEN
    //    val emptyViewEvent = viewModel.emptyGraph.getOrAwaitValue()
    //    //THEN
    //    Assert.assertTrue(!emptyViewEvent)
    //}

    @Test(expected = TimeoutException::class)
    fun when_ViewHeightIsNotGiven_Then_ItemsAreNotMapped() {
        (viewModel.getSmokingChartItems()).getOrAwaitValue()
    }


    //-------------------------------------------------------------------------------------------//
    //                                  C H A R T    I T E M S
    //-------------------------------------------------------------------------------------------//
    @Test
    fun maxConsumptionIsCorrectlyComputed() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val max = viewModel.maxQuantityLiveData.getOrAwaitValue()
        //THEN
        Assert.assertEquals(22, max)
    }

    @Test
    fun rightQuantityOfChartItemsDisplayed() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val items = viewModel.getSmokingChartItems().getOrAwaitValue()
        //THEN
        Assert.assertTrue(items.size == 8)
    }

    @Test
    fun cigaretteQuantitiesAreCorrectlyMapped() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val actualQuantities = viewModel.getSmokingChartItems().getOrAwaitValue().map { it.cigaretteNbr }.toTypedArray()
        val expectedQuantities = arrayOf(0, 0, 0, 0, 22, 0, 11, 0)
        //THEN
        Assert.assertArrayEquals(expectedQuantities, actualQuantities)
    }
    //-------------------------------------------------------------------------------------------//
    //                                     L O G    D A T A
    //-------------------------------------------------------------------------------------------//
    @Test
    fun threeLogDataAreCommunicated() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val logData = viewModel.getLogDataItems().getOrAwaitValue()
        //THEN
        Assert.assertEquals(3, logData.size)
    }

    @Test
    fun avoidedCigaretteNbrCorrectlyComputed() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val data = viewModel.getLogDataItems().getOrAwaitValue()[0]
        //THEN
        Assert.assertEquals("Mindfuled", data.description)
        Assert.assertEquals("37", data.value)
    }

    @Test
    fun moneySavedCorrectlyComputed() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val logData = viewModel.getLogDataItems().getOrAwaitValue()[1]
        //THEN
        Assert.assertEquals("Saved !", logData.description)
        Assert.assertEquals("37,00€", logData.value)
    }

    @Test
    fun averageConsumptionCorrectlyComputed() {
        //GIVEN
        viewModel.setMaxBarHeight(100)
        //WHEN
        val logData = viewModel.getLogDataItems().getOrAwaitValue()[2]
        //THEN
        Assert.assertEquals("Per day", logData.description)
        Assert.assertEquals("4,7", logData.value)
    }

    //-------------------------------------------------------------------------------------------//
    //                                  D U M M Y    D A T A
    //-------------------------------------------------------------------------------------------//
    private val dummyData = listOf(
        SmokingData(System.currentTimeMillis() - millisInDay, 11),
        SmokingData((System.currentTimeMillis() - millisInDay*3), 22)
    )

}