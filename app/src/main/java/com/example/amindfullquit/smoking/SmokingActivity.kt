package com.example.amindfullquit.smoking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amindfullquit.R

class SmokingActivity : AppCompatActivity(), SmokingChartAdapter.ItemClickListener {

    private lateinit var viewModel: SmokingDataViewModel

    private lateinit var chartRecyclerView: RecyclerView
    private lateinit var chartAdapter: SmokingChartAdapter

    private lateinit var chartDetailsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smoking)

        chartRecyclerView = findViewById(R.id.recycler_view_chart_smoking)
        chartDetailsTextView = findViewById(R.id.tv_details_chart_smoking)

        initChartRecyclerView()

        viewModel = ViewModelProviders.of(this)[SmokingDataViewModel::class.java]
        viewModel.getSmokingChartItems().observe(this, Observer { data ->
            chartAdapter.populateChart(data)
        })

        viewModel.cigarettesAvoided.observe(this, Observer {
            findViewById<TextView>(R.id.tv_number_smoking_log).text = it.toString()
        })

        chartRecyclerView.dimensions { height, _ ->
            viewModel.setMaxBarHeight(height)
        }
    }

    private fun initChartRecyclerView(){

        chartAdapter = SmokingChartAdapter(ArrayList(), this, this)
        val viewManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        chartRecyclerView.apply {
            setHasFixedSize(true)
            adapter = chartAdapter
            layoutManager = viewManager
        }
    }

    //VIEW SIZE HELPER, extension function
    private fun <T : View> T.dimensions(function: (Int, Int) -> Unit) {
        if (height == 0 || width == 0)
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    function(height, width)
                }
            })
        else function(height, width)
    }

    override fun onChartItemClick(position: Int) {
        chartDetailsTextView.text = viewModel.getChartDetails(position)
    }
}
