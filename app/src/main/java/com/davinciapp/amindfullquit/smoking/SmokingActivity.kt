package com.davinciapp.amindfullquit.smoking

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.davinciapp.amindfullquit.ChartOnScaleGestureListener
import com.davinciapp.amindfullquit.ChartOnScaleGestureListener.ZoomListener
import com.davinciapp.amindfullquit.R
import com.davinciapp.amindfullquit.RoundDataAdapter
import com.davinciapp.amindfullquit.di.ViewModelFactory
import com.davinciapp.amindfullquit.settings.SettingsActivity

class SmokingActivity : AppCompatActivity(), SmokingChartAdapter.ItemClickListener {

    private lateinit var viewModel: SmokingDataViewModel

    //VIEW
    private lateinit var chartRecyclerView: RecyclerView
    private lateinit var chartAdapter: SmokingChartAdapter
    private lateinit var dataAdapter: RoundDataAdapter

    private lateinit var chartDetailsTextView: TextView
    private lateinit var editButton: Button

    //VALUES
    private var currentSelection: SmokingChartItemUi? = null
    private var cigarettesQuantity = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smoking)

        setSupportActionBar(findViewById(R.id.toolbar_smoking_activity))
        supportActionBar?.title = ""

        chartRecyclerView = findViewById(R.id.recycler_view_chart_smoking)
        chartDetailsTextView = findViewById(R.id.tv_date_chart_smoking)
        editButton = findViewById(R.id.button_edit_chart_smoking)

        editButton.setOnClickListener { openNumberPickerDialog() }

        initChartRecyclerView()
        initRoundDataRecyclerView()

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[SmokingDataViewModel::class.java]

        //EMPTY VIEW
        viewModel.emptyGraph.observe(this, { empty ->
            if (empty) {
                findViewById<View>(R.id.layout_empty_view_chart_smoking).visibility = View.VISIBLE
                chartRecyclerView.visibility = View.GONE
            } else {
                findViewById<View>(R.id.layout_empty_view_chart_smoking).visibility = View.GONE
                chartRecyclerView.visibility = View.VISIBLE
            }
        })

        viewModel.getSmokingChartItems().observe(this, { data ->
            chartAdapter.populateChart(data)
            chartRecyclerView.scrollToPosition(data.size - 1)
        })

        viewModel.maxQuantityLiveData.observe(this, {
            findViewById<TextView>(R.id.text_view_smoke_chart_legend_max).text = it.toString()
        })

        viewModel.getLogDataItems().observe(this, {
            dataAdapter.populateData(it)
        })

        chartRecyclerView.dimensions { height, _ ->
            viewModel.setMaxBarHeight(height)
        }
    }

    //-------------------------------------------------------------------------------------------//
    //                                 R E C Y C L E R   V I E W S
    //-------------------------------------------------------------------------------------------//
    private fun initChartRecyclerView() {

        chartAdapter = SmokingChartAdapter(ArrayList(), this, this)
        val viewManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        //Zooming function
        val gestureListener = ChartOnScaleGestureListener(chartAdapter.barWidth)
        gestureListener.setZoomListener(object : ZoomListener {
            override fun onZooming(barWidth: Int) {
                chartAdapter.zoom(barWidth)
            }

            override fun zoomingStopped() {
                chartAdapter.isZooming = false
            }
        })
        val scaleGestureListener = ScaleGestureDetector(this, gestureListener)

        chartRecyclerView.apply {
            setHasFixedSize(true)
            adapter = chartAdapter
            layoutManager = viewManager

            setOnTouchListener { _, event ->
                scaleGestureListener.onTouchEvent(event)
                performClick()
                false //Still allow scrolling
            }
        }
    }

    private fun initRoundDataRecyclerView(){

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_round_smoking_data)
        dataAdapter = RoundDataAdapter(this)

        val helper = LinearSnapHelper()
        helper.attachToRecyclerView(recyclerView)


        recyclerView.apply {
            setHasFixedSize(true)
            adapter = dataAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    //-------------------------------------------------------------------------------------------//
    //                                        D I A L O G S
    //-------------------------------------------------------------------------------------------//
    private fun openNumberPickerDialog() {

        val picker = NumberPicker(this)
        picker.maxValue = 30
        picker.value = cigarettesQuantity


        val builder = AlertDialog.Builder(this)
        builder.setView(picker)
        builder.setPositiveButton("Ok") { dialog: DialogInterface, _ ->
            currentSelection?.let {
                viewModel.updateSmokingData(it.timeStamp, picker.value)
            }
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    //-------------------------------------------------------------------------------------------//
    //                                          M E N U
    //-------------------------------------------------------------------------------------------//
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_smoking, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_smoking_menu -> {
                startActivity(SettingsActivity.newInstance(this))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //-------------------------------------------------------------------------------------------//
    //                                        H E L P E R S
    //-------------------------------------------------------------------------------------------//
    //VIEW SIZE HELPER, extension function
    private fun <T : View> T.dimensions(function: (Int, Int) -> Unit) {
        if (height == 0 || width == 0)
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    function(height, width)
                }
            })
        else function(height, width)
    }

    //-------------------------------------------------------------------------------------------//
    //                                         C L I C K S
    //-------------------------------------------------------------------------------------------//
    override fun onChartItemClick(chartItem: SmokingChartItemUi) {
        currentSelection = chartItem
        chartDetailsTextView.text = chartItem.date
        cigarettesQuantity = chartItem.cigaretteNbr
        findViewById<TextView>(R.id.tv_quantity_chart_smoking).text = cigarettesQuantity.toString()
        editButton.visibility = View.VISIBLE
    }


}
